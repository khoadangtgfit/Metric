package com.hitachids.metriccollector.auth.security.encryption;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class CredentialEncryptor {
	private static final String ALGORITHM = "AES/GCM/NoPadding";
	private static final int GCM_IV_LENGTH = 12;
	private static final int GCM_TAG_LENGTH = 128;

	private final KeyProvider keyProvider;

	public CredentialEncryptor() {
		this.keyProvider = new KeyProvider();
	}

	/**
	 * Encrypt raw credential
	 * 
	 * @param rawCredential String
	 * @return String
	 */
	public String encrypt(String rawCredential) {
		try {
			// Generate a random IV
			byte[] iv = new byte[GCM_IV_LENGTH];

			new SecureRandom().nextBytes(iv);

			// Get encryption key
			SecretKey key = keyProvider.getSecretKey();

			// Initialize cipher for encryption
			Cipher cipher = Cipher.getInstance(ALGORITHM);

			GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

			cipher.init(Cipher.ENCRYPT_MODE, key, parameterSpec);

			// Encrypt the credential
			byte[] cipherText = cipher.doFinal(rawCredential.getBytes(StandardCharsets.UTF_8));

			// Combine IV and ciphertext
			ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);

			byteBuffer.put(iv);
			byteBuffer.put(cipherText);

			// Encode as Base64 string
			return Base64.getEncoder().encodeToString(byteBuffer.array());
		} catch (Exception ex) {
			throw new RuntimeException("Error encrypting credential: ", ex);
		}
	}

	/**
	 * Decrypt encrypted credential
	 * 
	 * @param encryptedCredential String
	 * @return String
	 */
	public String decrypt(String encryptedCredential) {
		try {
			// Decode from Base64
			byte[] encryptedData = Base64.getDecoder().decode(encryptedCredential);

			// Extract IV
			ByteBuffer byteBuffer = ByteBuffer.wrap(encryptedData);

			byte[] iv = new byte[GCM_IV_LENGTH];

			byteBuffer.get(iv);

			// Extract ciphertext
			byte[] cipherText = new byte[byteBuffer.remaining()];

			byteBuffer.get(cipherText);

			// Get encryption key
			SecretKey key = keyProvider.getSecretKey();

			// Initialize cipher for decryption
			Cipher cipher = Cipher.getInstance(ALGORITHM);

			GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);

			cipher.init(Cipher.DECRYPT_MODE, key, parameterSpec);

			// Decrypt the credential
			byte[] decryptedData = cipher.doFinal(cipherText);

			return new String(decryptedData, StandardCharsets.UTF_8);
		} catch (Exception ex) {
			throw new RuntimeException("Error decrypting credential: ", ex);
		}
	}
}