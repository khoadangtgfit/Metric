module metric_collector.metric_config {
    requires org.apache.commons.lang3;
    requires org.apache.commons.logging;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;

    requires transitive java.sql;
    requires static lombok;

    requires metric_collector.database_manager;
    requires metric_collector.common;
    requires metric_collector.storage;

    exports com.hitachids.metriccollector.metric.config.dto;
    exports com.hitachids.metriccollector.metric.config.model;
    exports com.hitachids.metriccollector.metric.config.service;

    provides com.hitachids.metriccollector.metric.config.service.MetricConfigService
            with com.hitachids.metriccollector.metric.config.service.impl.MetricConfigServiceImpl;
}
