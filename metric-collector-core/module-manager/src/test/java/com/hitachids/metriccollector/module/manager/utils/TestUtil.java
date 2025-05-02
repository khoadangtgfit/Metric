package com.hitachids.metriccollector.module.manager.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import com.hitachids.metriccollector.metric.config.model.MetricConfigModel;

public class TestUtil {
    public static File createTempFile(String fileName) throws IOException {
        Path tempDir = Files.createTempDirectory("test");
        File file = tempDir.resolve(fileName).toFile();
        file.createNewFile();
        file.deleteOnExit();
        tempDir.toFile().deleteOnExit();
        return file;
    }

    public static void setSystemProperty(String key, String value) {
        Properties props = System.getProperties();
        props.setProperty(key, value);
        System.setProperties(props);
    }

    public static MetricConfigModel createMockMetricConfig(String metricType, boolean enabled) {
        MetricConfigModel config = new MetricConfigModel();
        config.setMetricType(metricType);
        config.setEnabled(enabled);
        return config;
    }

    public static String createExecutableFile(String name) throws IOException {
        String extension = System.getProperty("os.name").toLowerCase().contains("win") ? ".bat" : ".sh";
        File file = createTempFile(name + extension);
        String command = System.getProperty("os.name").toLowerCase().contains("win")
                ? "@echo off\nping localhost -n 5 >nul"
                : "#!/bin/bash\nsleep 5";
        Files.write(file.toPath(), command.getBytes());
        file.setExecutable(true);
        return file.getAbsolutePath();
    }

    public static void createTestDirectory(String path) throws IOException {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    public static void cleanupTestDirectories(String path) {
        File directory = new File(path);
        if (directory.exists()) {
            deleteDirectory(directory);
        }
    }

    private static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }
}
