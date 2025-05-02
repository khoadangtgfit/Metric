package com.hitachids.metriccollector.cache;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hitachids.metriccollector.common.constant.Constants;
import com.hitachids.metriccollector.common.services.ServiceLocator;
import com.hitachids.metriccollector.db.DatabaseManager;
import com.hitachids.metriccollector.metric.config.model.MetricConfigModel;
import com.hitachids.metriccollector.metric.config.service.MetricConfigService;
import com.hitachids.metriccollector.scheduler.MetricScheduler;
import com.hitachids.metriccollector.scheduler.config.SchedulerConfig;

public class CacheMetricCollectorMain {
    private static final Log LOG = LogFactory.getLog(CacheMetricCollectorMain.class);
    private static final Map<Integer, Integer> METRIC_CONFIG_INTERVAL_VALUES = new HashMap<Integer, Integer>() {
        {
            put(Constants.METRIC_CONFIG_INTERVAL_5_SECONDS_VAL, 5);
            put(Constants.METRIC_CONFIG_INTERVAL_30_SECONDS_VAL, 30);
            put(Constants.METRIC_CONFIG_INTERVAL_1_MINUTE_VAL, 60);
            put(Constants.METRIC_CONFIG_INTERVAL_5_MINUTES_VAL, 300);
            put(Constants.METRIC_CONFIG_INTERVAL_1_HOUR_VAL, 3600);
            put(Constants.METRIC_CONFIG_INTERVAL_24_HOURS_VAL, 86400);
        }
    };

    public static void main(String[] args) throws Exception {
        MetricConfigService metricConfigService = ServiceLocator.getService(MetricConfigService.class);
        
        if (metricConfigService == null) {
            LOG.error(
                    "[Cache Metric Collector][CacheMetricCollectorMain] main, No MetricConfigService implementation found.");
            throw new RuntimeException("No MetricConfigService implementation found.");
        }
        
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        databaseManager.initialize();
        
        LOG.info("[Cache Metric Collector][CacheMetricCollectorMain] main, Database initialized successfully.");
        
        MetricConfigModel metricConfigModel = metricConfigService.getMetricConfigByMetricType(1, "cache").orElse(null);
        if (metricConfigModel == null) {
            LOG.error(
                    "[Cache Metric Collector][CacheMetricCollectorMain] main, no metric config found for cache-collector.");
            throw new RuntimeException("No Metric Config found for cache-collector.");
        }
        if (!metricConfigModel.isEnabled()) {
            LOG.warn("[Cache Metric Collector][CacheMetricCollectorMain] main, cache-collector is not enabled.");
            return;
        }
        try (MetricScheduler scheduler = new MetricScheduler(
                new SchedulerConfig(METRIC_CONFIG_INTERVAL_VALUES.get(metricConfigModel.getInterval()),
                        metricConfigModel.getMetricType()))) {
            scheduler.start();
            // Keep application running
            Thread.currentThread().join();
        } catch (Exception e) {
            LOG.error("[Cache Metric Collector][CacheMetricCollectorMain] main, error starting Cache Metric Collector",
                    e);
            System.exit(1);
        }
    }
}
