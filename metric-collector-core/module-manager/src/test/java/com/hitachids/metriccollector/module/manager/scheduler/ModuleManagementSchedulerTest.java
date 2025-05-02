package com.hitachids.metriccollector.module.manager.scheduler;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hitachids.metriccollector.metric.config.service.MetricConfigService;
import com.hitachids.metriccollector.module.manager.utils.ModuleUtils;
import com.hitachids.metriccollector.module.manager.utils.TestUtil;

@ExtendWith(MockitoExtension.class)
class ModuleManagementSchedulerTest {
    @Mock
    private MetricConfigService metricConfigService;
    @Mock
    private CollectorProcessManager collectorProcessManager;
    private ModuleManagementScheduler scheduler;
    private SchedulerConfig config;

    @BeforeEach
    void setUp() {
        config = new SchedulerConfig("test-dir", 1);
        scheduler = new ModuleManagementScheduler(metricConfigService, config, collectorProcessManager);

    }

    @Test
    void start_StartsScheduler() throws InterruptedException {
        scheduler.start();
        // Wait briefly to ensure scheduler started
        Thread.sleep(100);
        verify(metricConfigService, atLeastOnce()).getMetricConfigs(1);
    }

    @Test
    void run_HandlesEnabledAndDisabledConfigs() throws Exception {
        when(metricConfigService.getMetricConfigs(anyInt())).thenReturn(Arrays.asList(
                TestUtil.createMockMetricConfig("enabled-metric", true),
                TestUtil.createMockMetricConfig("disabled-metric", false)));
        when(collectorProcessManager.isCollectorRunning("enabled-metric")).thenReturn(false);
        when(collectorProcessManager.isCollectorRunning("disabled-metric")).thenReturn(true);        
        scheduler.start();
        Thread.sleep(1200);
        verify(collectorProcessManager, atLeastOnce()).isCollectorRunning("enabled-metric");
        verify(collectorProcessManager, atLeastOnce()).isCollectorRunning("disabled-metric");
        verify(collectorProcessManager, atLeastOnce()).stopCollector("disabled-metric");
    }

    @Test
    void handleEnabledConfigs_StartsNewProcess() throws Exception {
        when(collectorProcessManager.isCollectorRunning("enabled-metric")).thenReturn(false);
        try (MockedStatic<ModuleUtils> moduleUtilsMock = mockStatic(ModuleUtils.class)) {
            moduleUtilsMock.when(() -> ModuleUtils.findFileByName(anyString(), anyString()))
                    .thenReturn(Optional.of("/mock/path/collector.exe"));
            moduleUtilsMock.when(() -> ModuleUtils.getCollectorFileName("enabled-metric"))
                    .thenReturn("enabled-metric.exe");
            scheduler.handleEnabledConfigs(Arrays.asList(TestUtil.createMockMetricConfig("enabled-metric", true)));
            verify(collectorProcessManager).startCollector(eq("enabled-metric"), eq("/mock/path/collector.exe"));
        }
    }

    @Test
    void close_StopsScheduler() throws InterruptedException {
        scheduler.start();
        scheduler.close();
        Thread.sleep(1500);
        verify(metricConfigService, atMost(1)).getMetricConfigs(1);
    }
}
