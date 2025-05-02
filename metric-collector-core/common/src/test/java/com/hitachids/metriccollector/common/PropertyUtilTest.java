package com.hitachids.metriccollector.common;

import com.hitachids.metriccollector.common.utils.PropertyUtil;
import org.apache.commons.configuration2.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyUtilTest {

    @Mock
    private Configuration mockConfiguration;

    private Map<String, Configuration> configCache;

    @BeforeEach
    void setUp() throws Exception {
        // Initialize configCache
        configCache = new ConcurrentHashMap<>();

        // Clear and set up the static CONFIG_CACHE in PropertyUtil
        Field field = PropertyUtil.class.getDeclaredField("CONFIG_CACHE");
        field.setAccessible(true);

        @SuppressWarnings("unchecked")
        Map<String, Configuration> cache = (Map<String, Configuration>) field.get(null);
        cache.clear();
    }

    @Test
    void testGetStringWithDefaultFile() throws Exception {
        String key = "test.key";
        String expectedValue = "testValue";
        String defaultFilePath = "/config/application.properties";

        // Set the mocked configuration directly in PropertyUtil's CONFIG_CACHE
        Field field = PropertyUtil.class.getDeclaredField("CONFIG_CACHE");
        field.setAccessible(true);
        @SuppressWarnings("unchecked")
        Map<String, Configuration> cache = (Map<String, Configuration>) field.get(null);
        cache.put(defaultFilePath, mockConfiguration);

        when(mockConfiguration.getString(key)).thenReturn(expectedValue);

        // Act
        String result = PropertyUtil.getString(key);

        // Assert
        assertEquals(expectedValue, result);
        verify(mockConfiguration).getString(key);
    }

    @Test
    void testGetStringWithCustomFile() {
        String filePath = "/config/custom.properties";
        String key = "custom.key";
        String expectedValue = "customValue";
        configCache.put(filePath, mockConfiguration);
        when(mockConfiguration.getString(key)).thenReturn(expectedValue);

        // Set the mocked configuration in PropertyUtil's CONFIG_CACHE
        try {
            Field field = PropertyUtil.class.getDeclaredField("CONFIG_CACHE");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Configuration> cache = (Map<String, Configuration>) field.get(null);
            cache.put(filePath, mockConfiguration);
        } catch (Exception e) {
            fail("Failed to set CONFIG_CACHE: " + e.getMessage());
        }

        // Act
        String result = PropertyUtil.getString(filePath, key);

        // Assert
        assertEquals(expectedValue, result);
        verify(mockConfiguration).getString(key);
    }

    @Test
    void testGetStringWithDefaultValue() {
        // Arrange
        String filePath = "/config/custom.properties";
        String key = "nonexistent.key";
        String defaultValue = "defaultValue";
        configCache.put(filePath, mockConfiguration);
        when(mockConfiguration.getString(key, defaultValue)).thenReturn(defaultValue);

        // Set the mocked configuration in PropertyUtil's CONFIG_CACHE
        try {
            Field field = PropertyUtil.class.getDeclaredField("CONFIG_CACHE");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Configuration> cache = (Map<String, Configuration>) field.get(null);
            cache.put(filePath, mockConfiguration);
        } catch (Exception e) {
            fail("Failed to set CONFIG_CACHE: " + e.getMessage());
        }

        // Act
        String result = PropertyUtil.getString(filePath, key, defaultValue);

        // Assert
        assertEquals(defaultValue, result);
        verify(mockConfiguration).getString(key, defaultValue);
    }

    @Test
    void testGetIntWithCustomFile() {
        String filePath = "/config/custom.properties";
        String key = "int.key";
        int expectedValue = 42;
        configCache.put(filePath, mockConfiguration);
        when(mockConfiguration.getInt(key)).thenReturn(expectedValue);

        // Set the mocked configuration in PropertyUtil's CONFIG_CACHE
        try {
            Field field = PropertyUtil.class.getDeclaredField("CONFIG_CACHE");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Configuration> cache = (Map<String, Configuration>) field.get(null);
            cache.put(filePath, mockConfiguration);
        } catch (Exception e) {
            fail("Failed to set CONFIG_CACHE: " + e.getMessage());
        }

        // Act
        Integer result = PropertyUtil.getInt(filePath, key);

        // Assert
        assertEquals(expectedValue, result);
        verify(mockConfiguration).getInt(key);
    }

    @Test
    void testGetIntWithDefaultValue() {
        String filePath = "/config/custom.properties";
        String key = "nonexistent.int.key";
        int defaultValue = 100;
        configCache.put(filePath, mockConfiguration);
        when(mockConfiguration.getInt(key, defaultValue)).thenReturn(defaultValue);

        // Set the mocked configuration in PropertyUtil's CONFIG_CACHE
        try {
            Field field = PropertyUtil.class.getDeclaredField("CONFIG_CACHE");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Configuration> cache = (Map<String, Configuration>) field.get(null);
            cache.put(filePath, mockConfiguration);
        } catch (Exception e) {
            fail("Failed to set CONFIG_CACHE: " + e.getMessage());
        }

        // Act
        Integer result = PropertyUtil.getInt(filePath, key, defaultValue);

        // Assert
        assertEquals(defaultValue, result);
        verify(mockConfiguration).getInt(key, defaultValue);
    }

    @Test
    void testGetBooleanWithCustomFile() {
        String filePath = "/config/custom.properties";
        String key = "boolean.key";
        boolean expectedValue = true;
        configCache.put(filePath, mockConfiguration);
        when(mockConfiguration.getBoolean(key)).thenReturn(expectedValue);

        // Set the mocked configuration in PropertyUtil's CONFIG_CACHE
        try {
            Field field = PropertyUtil.class.getDeclaredField("CONFIG_CACHE");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Configuration> cache = (Map<String, Configuration>) field.get(null);
            cache.put(filePath, mockConfiguration);
        } catch (Exception e) {
            fail("Failed to set CONFIG_CACHE: " + e.getMessage());
        }

        // Act
        Boolean result = PropertyUtil.getBoolean(filePath, key);

        // Assert
        assertEquals(expectedValue, result);
        verify(mockConfiguration).getBoolean(key);
    }

    @Test
    void testGetBooleanWithDefaultValue() {
        String filePath = "/config/custom.properties";
        String key = "nonexistent.boolean.key";
        boolean defaultValue = false;
        configCache.put(filePath, mockConfiguration);
        when(mockConfiguration.getBoolean(key, defaultValue)).thenReturn(defaultValue);

        // Set the mocked configuration in PropertyUtil's CONFIG_CACHE
        try {
            Field field = PropertyUtil.class.getDeclaredField("CONFIG_CACHE");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Configuration> cache = (Map<String, Configuration>) field.get(null);
            cache.put(filePath, mockConfiguration);
        } catch (Exception e) {
            fail("Failed to set CONFIG_CACHE: " + e.getMessage());
        }

        // Act
        Boolean result = PropertyUtil.getBoolean(filePath, key, defaultValue);

        // Assert
        assertEquals(defaultValue, result);
        verify(mockConfiguration).getBoolean(key, defaultValue);
    }

    @Test
    void testCacheBehavior() {
        String filePath = "/config/test.properties";
        String key = "test.key";
        String expectedValue = "cachedValue";
        configCache.put(filePath, mockConfiguration);
        when(mockConfiguration.getString(key)).thenReturn(expectedValue);

        // Set the mocked configuration in PropertyUtil's CONFIG_CACHE
        try {
            Field field = PropertyUtil.class.getDeclaredField("CONFIG_CACHE");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            Map<String, Configuration> cache = (Map<String, Configuration>) field.get(null);
            cache.put(filePath, mockConfiguration);
        } catch (Exception e) {
            fail("Failed to set CONFIG_CACHE: " + e.getMessage());
        }

        // Act
        String result1 = PropertyUtil.getString(filePath, key);
        String result2 = PropertyUtil.getString(filePath, key);

        // Assert
        assertEquals(expectedValue, result1);
        assertEquals(expectedValue, result2);
        verify(mockConfiguration, times(2)).getString(key);
    }

    @Test
    void testConfigurationNotFound() {
        // Arrange
        String filePath = "/config/nonexistent.properties";
        String key = "test.key";

        // Act
        String result = PropertyUtil.getString(filePath, key);

        // Assert
        assertNull(result);
    }

    @Test
    void testGetDefaultConfigFile() throws Exception {
        System.setProperty("config.file", "test.properties");

        // Act
        Method method = PropertyUtil.class.getDeclaredMethod("getDefaultConfigFile");
        method.setAccessible(true); // Allow access to private method
        String result = (String) method.invoke(null); // Invoke static method with no instance

        // Assert
        assertEquals("/config/test.properties", result);

        // Cleanup
        System.clearProperty("config.file");
    }
}