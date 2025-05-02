module metric_collector.cache_collector {
    requires metric_collector.scheduler;
    requires metric_collector.metric_config;
    requires metric_collector.resiliencer;
    requires metric_collector.queue_messager;
    requires metric_collector.log_service;
    requires metric_collector.common;
    requires metric_collector.database_manager;
    requires org.apache.commons.logging;

    uses com.hitachids.metriccollector.metric.config.service.MetricConfigService;
}
