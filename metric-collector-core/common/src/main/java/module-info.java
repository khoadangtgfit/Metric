module metric_collector.common {
    requires org.apache.commons.lang3;
    requires org.apache.commons.logging;
    requires org.apache.commons.configuration2;

    requires transitive java.sql;
    requires static lombok;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    exports com.hitachids.metriccollector.common.constant;
    exports com.hitachids.metriccollector.common.entity.base;
    exports com.hitachids.metriccollector.common.model.base;
    exports com.hitachids.metriccollector.common.services;
    exports com.hitachids.metriccollector.common.utils;
    exports com.hitachids.metriccollector.common.exception;
    exports com.hitachids.metriccollector.common.security;
}
