package com.hitachids.metriccollector.auth.security.encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.Properties;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class KeyProvider {
	private static final Log LOG = LogFactory.getLog(KeyProvider.class);
	private static final String KEY_ALGORITHM = "AES";
	private static final String DEFAULT_CONFIG_PATH = "/config";
	private static final String KEY_STORE_PATH = "security.encryption.key";

	/**
	 * Get secret key
	 * 
	 * @return SecretKey
	 */
	public SecretKey getSecretKey() {
		String configFile = System.getProperty("config.file", "application.properties");
		return getSecretKey(DEFAULT_CONFIG_PATH + "/" + configFile);
	}

	/**
	 * Get secret key
	 * 
	 * @param encodedKey String
	 * @return SecretKey
	 */
	public SecretKey getSecretKey(String path) {
		SecretKey secretKey = null;

		try {
			File configFile = getConfigFile(path);

			if (configFile != null) {
				secretKey = getKeyFromConfigFile(configFile);
			}
		} catch (Exception ex) {
			throw new RuntimeException("Error accessing encryption key: ", ex);
		}

		return secretKey;
	}

	private File getConfigFile(String path) {
		try {
			File externalFile = new File(new File("").getAbsolutePath().concat(path));

			LOG.info("Loading configuration from directory: " + externalFile.getAbsolutePath());

			if (externalFile.exists() && externalFile.canRead()) {
				LOG.info("Loaded configuration from directory: " + externalFile.getAbsolutePath());
				return externalFile;
			}

			// Fallback: try as absolute file
			String fallbackPath = "/metric-collector-core" + path;

			File fallbackExternalFile = new File(new File("").getAbsolutePath().concat(fallbackPath));

			LOG.info("Loading configuration from fallback directory: " + fallbackExternalFile.getAbsolutePath());

			if (fallbackExternalFile.exists() && fallbackExternalFile.canRead()) {
				LOG.info("Loaded configuration from fallback directory: " + fallbackExternalFile.getAbsolutePath());
				return fallbackExternalFile;
			}
		} catch (Exception ex) {
			throw new RuntimeException("Failed to load configuration from directory: ", ex);
		}

		return null;
	}

	/**
	 * Get key from config file
	 * 
	 * @param configFile File
	 * @return SecretKey
	 */
	private SecretKey getKeyFromConfigFile(File configFile) {
		try {
			// Load properties from the file
			Properties properties = new Properties();

			try (FileInputStream inputStream = new FileInputStream(configFile)) {
				properties.load(inputStream);
			}

			// Get the key property
			String encodedKey = properties.getProperty(KEY_STORE_PATH);

			if (encodedKey == null || encodedKey.trim().isEmpty()) {
				// Key not found in the file, generate a new one
				KeyGenerator keyGenerator = KeyGenerator.getInstance(KEY_ALGORITHM);

				keyGenerator.init(256);

				SecretKey newKey = keyGenerator.generateKey();

				// Save the new key to the file
				String newEncodedKey = Base64.getEncoder().encodeToString(newKey.getEncoded());

				properties.setProperty(KEY_STORE_PATH, newEncodedKey);

				return newKey;
			} else {
				// Decode the existing key
				byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
				return new SecretKeySpec(decodedKey, KEY_ALGORITHM);
			}
		} catch (Exception ex) {
			throw new RuntimeException("Error accessing encryption key in config file: ", ex);
		}
	}
}