package com.hitachids.metriccollector.module.manager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hitachids.metriccollector.metric.config.service.MetricConfigService;

@ExtendWith(MockitoExtension.class)
class ModuleManagerTest {
    private ModuleManager moduleManager;

    @BeforeEach
    void setUp() {
        moduleManager = ModuleManager.getInstance();
    }

    @Test
    void getInstance_ReturnsSingleInstance() {
        ModuleManager instance1 = ModuleManager.getInstance();
        ModuleManager instance2 = ModuleManager.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    void loadModules_LoadsAndStartsScheduler() {
        assertDoesNotThrow(() -> moduleManager.loadModules());
    }

    @Test
    void close_ClosesScheduler() {
        moduleManager.loadModules();
        assertDoesNotThrow(() -> moduleManager.close());
    }
}
