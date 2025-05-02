module metric_collector.database_manager {
    requires metric_collector.common;

    requires org.apache.commons.lang3;
    requires org.apache.commons.logging;
    requires org.xerial.sqlitejdbc;

    requires transitive java.sql;

    exports com.hitachids.metriccollector.db;
    exports com.hitachids.metriccollector.db.schema;
    exports com.hitachids.metriccollector.db.repository;
    exports com.hitachids.metriccollector.db.repository.impl;
}
