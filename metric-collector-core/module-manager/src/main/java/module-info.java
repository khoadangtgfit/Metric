module metric_collector.module_manager {
    requires metric_collector.metric_config;
    requires metric_collector.common;
    requires org.apache.commons.logging;
    requires static lombok;

    uses com.hitachids.metriccollector.metric.config.service.MetricConfigService;
    exports com.hitachids.metriccollector.module.manager;
}
