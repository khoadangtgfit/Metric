module metric_collector.storage {
    requires org.apache.commons.lang3;
    requires org.apache.commons.logging;

    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;

    requires transitive java.sql;
    requires static lombok;

    requires metric_collector.database_manager;
    requires metric_collector.common;
    requires metric_collector.authentication;

    exports com.hitachids.metriccollector.storage.dto;
    exports com.hitachids.metriccollector.storage.entity;
    exports com.hitachids.metriccollector.storage.model;
    exports com.hitachids.metriccollector.storage.service;

    provides com.hitachids.metriccollector.storage.service.StorageService
            with com.hitachids.metriccollector.storage.service.impl.StorageServiceImpl;

    exports com.hitachids.metriccollector.storage.mapper.impl
            to metric_collector.metric_config;

    exports com.hitachids.metriccollector.storage.repository.impl
            to metric_collector.metric_config;
}
