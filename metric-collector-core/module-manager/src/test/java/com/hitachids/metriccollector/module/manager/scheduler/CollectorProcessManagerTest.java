package com.hitachids.metriccollector.module.manager.scheduler;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.hitachids.metriccollector.module.manager.utils.TestUtil;

class CollectorProcessManagerTest {
    private CollectorProcessManager processManager;

    @BeforeEach
    void setUp() {
        processManager = new CollectorProcessManager();
    }

    @Test
    void startCollector_StartsNewProcess() throws Exception {
        String metricType = "test-metric";
        String binaryPath = TestUtil.createExecutableFile("test-collector");

        processManager.startCollector(metricType, binaryPath);
        assertTrue(processManager.isCollectorRunning(metricType));
    }

    @Test
    void stopCollector_StopsRunningProcess() throws Exception {
        String metricType = "test-metric";
        String binaryPath = TestUtil.createExecutableFile("test-collector");

        processManager.startCollector(metricType, binaryPath);
        processManager.stopCollector(metricType);
        assertFalse(processManager.isCollectorRunning(metricType));
    }

    @Test
    void cleanupDeadProcesses_RemovesDeadProcesses() throws IOException {
        Process process = mock(Process.class);
        when(process.isAlive()).thenReturn(false);
        processManager.getRunningCollectors().put("test-metric", process);
        processManager.cleanupDeadProcesses();
        assertEquals(0, processManager.getRunningCollectors().size());
    }

    @Test
    void stopCollectorsForDisabled_StopsAllDisabledCollectors() throws Exception {
        String metricType = "test-metric";
        String binaryPath = TestUtil.createExecutableFile("test-collector");

        processManager.startCollector(metricType, binaryPath);
        processManager.stopCollectorsForDisabled(Arrays.asList(metricType));
        assertFalse(processManager.isCollectorRunning(metricType));
    }

    @Test
    void stopAllCollectors_StopsAllRunningProcesses() throws Exception {
        // Setup multiple collectors
        String metricType1 = "test-metric-1";
        String metricType2 = "test-metric-2";
        String binaryPath = TestUtil.createExecutableFile("test-collector");

        processManager.startCollector(metricType1, binaryPath);
        processManager.startCollector(metricType2, binaryPath);

        // Verify collectors are running
        assertTrue(processManager.isCollectorRunning(metricType1));
        assertTrue(processManager.isCollectorRunning(metricType2));

        // Stop all collectors
        processManager.stopAllCollectors();

        // Verify all collectors are stopped
        assertFalse(processManager.isCollectorRunning(metricType1));
        assertFalse(processManager.isCollectorRunning(metricType2));
        assertEquals(0, processManager.getRunningCollectors().size());
    }
}
