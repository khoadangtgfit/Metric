package com.hitachids.metriccollector.module.manager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hitachids.metriccollector.common.services.ServiceLocator;
import com.hitachids.metriccollector.metric.config.service.MetricConfigService;
import com.hitachids.metriccollector.module.manager.scheduler.CollectorProcessManager;
import com.hitachids.metriccollector.module.manager.scheduler.ModuleManagementScheduler;
import com.hitachids.metriccollector.module.manager.scheduler.SchedulerConfig;

public class ModuleManager implements AutoCloseable {
    private static final Log LOG = LogFactory.getLog(ModuleManager.class);
    private static volatile ModuleManager instance;
    
    private ModuleManagementScheduler scheduler;
    
    private ModuleManager() {}
    
    public static ModuleManager getInstance() {
        if (instance == null) {
            synchronized (ModuleManager.class) {
                if (instance == null) {
                    instance = new ModuleManager();
                }
            }
        }
        return instance;
    }

    public void loadModules() {
        MetricConfigService metricConfigService = ServiceLocator.getService(MetricConfigService.class);
        
        if (metricConfigService == null) {
            LOG.error("[Module Manager][ModuleManager] loadModules, No MetricConfigService implementation found.");
            throw new RuntimeException("No MetricConfigService implementation found.");
        }
        
        LOG.info("[Module Manager][ModuleManager] loadModules, Loaded MetricConfigService: " + metricConfigService.getClass().getName());

        SchedulerConfig config = SchedulerConfig.fromProperties();
        scheduler = new ModuleManagementScheduler(metricConfigService, config, new CollectorProcessManager());
        scheduler.start();
    }

    @Override
    public void close() {
        if (scheduler != null) {
            scheduler.close();
        }
    }
}
