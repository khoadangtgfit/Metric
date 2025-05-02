package com.hitachids.metriccollector.common.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PropertyUtil {
    private static final Log LOG = LogFactory.getLog(PropertyUtil.class);
    private static final Map<String, Configuration> CONFIG_CACHE = new ConcurrentHashMap<>();
    private static final String DEFAULT_CONFIG_PATH = "/config";
    private static final String DEFAULT_CONFIG_FILE = "application.properties"; // Updated for consistency: Extracted constant

    public static String getString(String key) {
        return getString(getDefaultConfigFile(), key);
    }

    public static String getString(String filePath, String key) {
        Configuration config = getConfiguration(filePath);
        return config != null ? config.getString(key) : null;
    }

    public static String getString(String filePath, String key, String defaultValue) {
        Configuration config = getConfiguration(filePath);
        return config != null ? config.getString(key, defaultValue) : defaultValue;
    }

    public static Integer getInt(String key) {
        return getInt(getDefaultConfigFile(), key);
    }

    public static Integer getInt(String filePath, String key) {
        Configuration config = getConfiguration(filePath);
        return config != null ? config.getInt(key) : null;
    }

    public static Integer getInt(String filePath, String key, int defaultValue) {
        Configuration config = getConfiguration(filePath);
        return config != null ? config.getInt(key, defaultValue) : defaultValue;
    }

    // Added for getMaxBackoffMs support
    public static Long getLong(String key) {
        return getLong(getDefaultConfigFile(), key, null);
    }

    // Added for getMaxBackoffMs support
    public static Long getLong(String filePath, String key) {
        return getLong(filePath, key, null);
    }

    // Added for getMaxBackoffMs support
    public static Long getLong(String filePath, String key, Long defaultValue) {
        Configuration config = getConfiguration(filePath);
        if (config == null) return defaultValue;
        try {
            return config.getLong(key, defaultValue);
        } catch (Exception e) {
            LOG.warn("Invalid long value for key: " + key + ", using default: " + defaultValue, e);
            return defaultValue;
        }
    }

    public static Boolean getBoolean(String key) {
        return getBoolean(getDefaultConfigFile(), key);
    }

    public static Boolean getBoolean(String filePath, String key) {
        Configuration config = getConfiguration(filePath);
        return config != null ? config.getBoolean(key) : null;
    }

    public static Boolean getBoolean(String filePath, String key, boolean defaultValue) {
        Configuration config = getConfiguration(filePath);
        return config != null ? config.getBoolean(key, defaultValue) : defaultValue;
    }

    private static Configuration getConfiguration(String filePath) {
        return CONFIG_CACHE.computeIfAbsent(filePath, path -> {
            // Try as absolute file
            File externalFile = new File(new File("").getAbsolutePath().concat(path));

            LOG.info("Attempting to load configuration from: " + externalFile.getAbsolutePath()); // Updated for consistency: Standardized log message

            if (externalFile.exists() && externalFile.canRead()) {
                return loadFromFile(externalFile);
            }

            // Fallback: try as absolute file
            String fallbackPath = "/metric-collector-core" + path;

            File fallbackExternalFile = new File(new File("").getAbsolutePath().concat(fallbackPath));

            LOG.info("Attempting to load configuration from fallback: " + fallbackExternalFile.getAbsolutePath()); // Updated for consistency: Standardized log message

            if (fallbackExternalFile.exists() && fallbackExternalFile.canRead()) {
                return loadFromFile(fallbackExternalFile);
            }

            LOG.warn("Configuration file not found: " + externalFile.getAbsolutePath() + " or " + fallbackExternalFile.getAbsolutePath()); // Updated for consistency: Improved warning message
            return null;
        });
    }

    private static Configuration loadFromFile(File file) {
        Configuration config = null;

        try {
            Configurations configs = new Configurations();
            config = configs.properties(file);
            LOG.info("Successfully loaded configuration from: " + file.getAbsolutePath()); // Updated for consistency: Standardized log message
        } catch (ConfigurationException ex) {
            LOG.warn("Failed to load configuration from: " + file.getAbsolutePath(), ex); // Updated for consistency: Standardized log message
        }

        return config;
    }

    private static String getDefaultConfigFile() {
        String configFile = System.getProperty("config.file", DEFAULT_CONFIG_FILE);
        return DEFAULT_CONFIG_PATH + "/" + configFile;
    }
}