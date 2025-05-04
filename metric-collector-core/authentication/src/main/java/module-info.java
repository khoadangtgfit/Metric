module metric_collector.authentication {
    requires java.net.http;
    requires org.apache.commons.lang3;
    requires org.apache.commons.logging;

    requires metric_collector.common;
    requires com.fasterxml.jackson.annotation;
    requires lombok;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires metric_collector.resiliencer;
    requires metric_collector.storage;
    requires org.apache.commons.configuration2;

    exports com.hitachids.metriccollector.auth.service;
    exports com.hitachids.metriccollector.auth.service.impl;

}
