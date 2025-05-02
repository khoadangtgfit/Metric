module metric_collector.app {
    requires metric_collector.module_manager;
    requires metric_collector.database_manager;
    requires metric_collector.common;
    // Add other required modules
    requires org.apache.commons.logging;
}
