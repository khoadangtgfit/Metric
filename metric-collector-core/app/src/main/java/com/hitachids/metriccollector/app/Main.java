package com.hitachids.metriccollector.app;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hitachids.metriccollector.db.DatabaseManager;
import com.hitachids.metriccollector.module.manager.ModuleManager;

public class Main {
        private static final Log LOG = LogFactory.getLog(Main.class);

        public static void main(String[] args) throws Exception {
                LOG.info("[App][Main] main, Starting Metric Collector Application");

                DatabaseManager databaseManager = DatabaseManager.getInstance();
                databaseManager.initialize();

                ModuleManager moduleManager = ModuleManager.getInstance();
                moduleManager.loadModules();
        }

}
