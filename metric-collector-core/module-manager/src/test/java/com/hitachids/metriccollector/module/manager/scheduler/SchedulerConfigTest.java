package com.hitachids.metriccollector.module.manager.scheduler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.hitachids.metriccollector.common.utils.ConfigurationUtil;

class SchedulerConfigTest {

    @BeforeEach
    void setUp() {
        System.clearProperty("collectors.binary.path");
        System.clearProperty("collector.scheduler.interval.seconds");
    }

    @Test
    void fromProperties_WithDefaultValues_ReturnsDefaultConfig() {
        SchedulerConfig config = SchedulerConfig.fromProperties();

        assertEquals("collector-binaries", config.getBaseDirectory());
        assertEquals(60, config.getIntervalSeconds());
    }

    @Test
    void fromProperties_WithCustomValues_ReturnsCustomConfig() {
        try (MockedStatic<ConfigurationUtil> mockedStatic = mockStatic(ConfigurationUtil.class)) {
            mockedStatic.when(() -> ConfigurationUtil.getCollectorBinariesDirectory()).thenReturn("custom/path");
            mockedStatic.when(() -> ConfigurationUtil.getCollectorSchedulerInterval()).thenReturn(30);

            SchedulerConfig config = SchedulerConfig.fromProperties();

            assertEquals("custom/path", config.getBaseDirectory());
            assertEquals(30, config.getIntervalSeconds());
        }
    }
}
