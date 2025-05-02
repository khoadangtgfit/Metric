module metric_collector.authentication {
    requires org.apache.commons.lang3;
    requires org.apache.commons.logging;

    requires metric_collector.common;
    requires metric_collector.storage;
    requires com.fasterxml.jackson.annotation;
    requires lombok;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires metric_collector.resiliencer;

    exports com.hitachids.metriccollector.auth.security.encryption;
}
