package com.hitachids.metriccollector.module.manager.scheduler;

import com.hitachids.metriccollector.metric.config.service.MetricConfigService;
import com.hitachids.metriccollector.metric.config.model.MetricConfigModel;
import com.hitachids.metriccollector.module.manager.utils.ModuleUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;
import java.util.concurrent.*;

public class ModuleManagementScheduler implements AutoCloseable {
    private static final Log LOG = LogFactory.getLog(ModuleManagementScheduler.class);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final CollectorProcessManager processManager;
    private final MetricConfigService metricConfigService;
    private final SchedulerConfig schedulerConfig;

    public ModuleManagementScheduler(MetricConfigService metricConfigService, SchedulerConfig config, CollectorProcessManager processManager) {
        this.metricConfigService = metricConfigService;
        this.schedulerConfig = config;
        this.processManager = processManager;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::run, 0, schedulerConfig.getIntervalSeconds(), TimeUnit.SECONDS);
        LOG.info("[Module Manager][ModuleManagementScheduler] start, started ModuleManagementScheduler with interval: "
                + schedulerConfig.getIntervalSeconds() + " seconds");
    }

    private void run() {
        try {
            processManager.cleanupDeadProcesses();

            List<MetricConfigModel> configs = metricConfigService.getMetricConfigs(1);
            LOG.info("[Module Manager][ModuleManagementScheduler] run, fetched metric configs: " + configs.size());
            List<MetricConfigModel> enabled = new ArrayList<>();
            List<MetricConfigModel> disabled = new ArrayList<>();

            for (MetricConfigModel config : configs) {
                if (config.isEnabled()) {
                    enabled.add(config);
                } else {
                    disabled.add(config);
                }
            }

            handleEnabledConfigs(enabled);
            handleDisabledConfigs(disabled);

        } catch (Exception e) {
            LOG.error("[Module Manager][ModuleManagementScheduler] run, error in scheduler run", e);
        }
    }

    protected void handleEnabledConfigs(List<MetricConfigModel> enabled) {
        LOG.info("[Module Manager][ModuleManagementScheduler] handleEnabledConfigs, processing enabled configs: "
                + enabled.size());
        for (MetricConfigModel config : enabled) {
            String metricType = config.getMetricType();
            if (!processManager.isCollectorRunning(metricType)) {
                Optional<String> binaryPath = ModuleUtils.findFileByName(schedulerConfig.getBaseDirectory(),
                        ModuleUtils.getCollectorFileName(metricType));

                binaryPath.ifPresentOrElse(path -> {
                    try {
                        processManager.startCollector(metricType, path);
                    } catch (Exception e) {
                        LOG.error(
                                "[Module Manager][ModuleManagementScheduler] handleEnabledConfigs, failed to start collector for metric: "
                                        + metricType,
                                e);
                    }
                }, () -> {
                    LOG.error(
                            "[Module Manager][ModuleManagementScheduler] handleEnabledConfigs, Collector binary not found for metric: "
                                    + metricType);
                });
            } else {
                LOG.info(
                        "[Module Manager][ModuleManagementScheduler] handleEnabledConfigs, Collector already running for metric: "
                                + metricType);
            }
        }
    }

    protected void handleDisabledConfigs(List<MetricConfigModel> disabled) {
        LOG.info("[Module Manager][ModuleManagementScheduler] handleDisabledConfigs, processing disabled configs: "
                + disabled.size());
        for (MetricConfigModel config : disabled) {
            try {
                if (!processManager.isCollectorRunning(config.getMetricType())) {
                    LOG.info(
                            "[Module Manager][ModuleManagementScheduler] handleDisabledConfigs, Collector not running for metric: "
                                    + config.getMetricType());
                    continue;
                }
                processManager.stopCollector(config.getMetricType());
            } catch (Exception e) {
                LOG.error(
                        "[Module Manager][ModuleManagementScheduler] handleDisabledConfigs, failed to stop collector for metric: "
                                + config.getMetricType(),
                        e);
            }
        }

    }

    @Override
    public void close() {
        scheduler.shutdownNow();
        try {
            scheduler.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
