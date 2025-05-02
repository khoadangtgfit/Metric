package com.hitachids.metriccollector.module.manager.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Path;
import java.util.Optional;
import java.io.File;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

class ModuleUtilsTest {
    private static final String BASE_DIR = "test/";

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        System.setProperty("os.name", "Windows 10");
    }

    @Test
    void findFileByName_WhenFallbackFileExists_ReturnsFallbackPath() throws IOException {
        String currentPath = new File("").getAbsolutePath();
        TestUtil.createTestDirectory(currentPath.concat("/metric-collector-core"));
        TestUtil.createTestDirectory(currentPath.concat("/metric-collector-core/" + BASE_DIR));
        String fileName = "test.txt";
        File testFile = new File(currentPath + "/metric-collector-core/" + BASE_DIR, fileName);
        testFile.createNewFile();
        Optional<String> result = ModuleUtils.findFileByName(BASE_DIR, fileName);

        assertTrue(result.isPresent());
        TestUtil.cleanupTestDirectories(currentPath.concat("/metric-collector-core"));
    }

    @Test
    void findFileByName_WhenFilesExist_ReturnsFirstPath() throws IOException {
        String fileName = "test.txt";
        String currentPath = new File("").getAbsolutePath();
        TestUtil.createTestDirectory(currentPath.concat("/" + BASE_DIR));
        File testFile = new File(currentPath + "/" + BASE_DIR, fileName);
        testFile.createNewFile();

        Optional<String> result = ModuleUtils.findFileByName(BASE_DIR, fileName);
        assertTrue(result.isPresent());
        TestUtil.cleanupTestDirectories(currentPath.concat("/" + BASE_DIR));
    }

    @Test
    void findFileByName_WhenFileDoesNotExist_ReturnsEmpty() {
        Optional<String> result = ModuleUtils.findFileByName("nonexistent/", "nonexistent.txt");
        assertTrue(result.isEmpty());
    }

    @Test
    void getCollectorFileName_OnWindows_ReturnsExeExtension() {
        TestUtil.setSystemProperty("os.name", "Windows 10");
        assertEquals("test-metric-collector.exe", ModuleUtils.getCollectorFileName("test"));
    }

    @Test
    void getCollectorFileName_OnLinux_ReturnsNoExtension() {
        TestUtil.setSystemProperty("os.name", "Linux");
        assertEquals("test-metric-collector", ModuleUtils.getCollectorFileName("test"));
    }

    @Test
    void getCollectorFileName_OnMac_ReturnsNoExtension() {
        TestUtil.setSystemProperty("os.name", "Mac OS X");
        assertEquals("test-metric-collector", ModuleUtils.getCollectorFileName("test"));
    }
}
