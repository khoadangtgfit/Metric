package com.hitachids.metriccollector.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hitachids.metriccollector.common.utils.ConfigurationUtil;
import com.hitachids.metriccollector.db.connection.ConnectionManager;
import com.hitachids.metriccollector.db.connection.SQLiteConnectionProvider;
import com.hitachids.metriccollector.db.schema.SQLiteSchemaProvider;
import com.hitachids.metriccollector.db.schema.SchemaManager;
import com.hitachids.metriccollector.db.transaction.SQLiteTransactionTemplate;
import com.hitachids.metriccollector.db.transaction.TransactionManager;

public class DatabaseManager {
    private static final Log LOG = LogFactory.getLog(DatabaseManager.class);
    private static final Thread CURRENT_THREAD = Thread.currentThread();
    private static final String DIRECTORY_PATH = "/db/scripts/structure/";

    private static DatabaseManager instance;
    private final Map<String, DatabaseContext> contexts = new HashMap<>();
    private String defaultContextName = "default";

    private DatabaseManager() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }

        return instance;
    }

    public synchronized void initialize() throws Exception {
        initialize(defaultContextName, ConfigurationUtil.getDatabasePath());
    }

    public synchronized void initialize(String path) throws Exception {
        initialize(defaultContextName, path);
    }

    public synchronized void initialize(String contextName, String path) throws Exception {
        if (contexts.containsKey(contextName)) {
            return;
        }

        ConnectionManager connectionManager = initializeConnection(path);
        TransactionManager transactionManager = new SQLiteTransactionTemplate(connectionManager);
        SchemaManager schemaManager = SQLiteSchemaProvider.create(connectionManager);

        initializeSchema(schemaManager);

        DatabaseContext context = new DatabaseContext(connectionManager, transactionManager, schemaManager);
        contexts.put(contextName, context);
    }

    public DatabaseContext getContext() {
        return getContext(defaultContextName);
    }

    public DatabaseContext getContext(String contextName) {
        DatabaseContext context = contexts.get(contextName);

        if (context == null) {
            throw new IllegalStateException("Database context '" + contextName + "' not initialized.");
        }

        return context;
    }

    public Connection getConnection() throws SQLException {
        return getContext().getConnectionManager().getConnection();
    }

    public Connection getConnection(String contextName) throws SQLException {
        return getContext(contextName).getConnectionManager().getConnection();
    }

    public TransactionManager getTransactionManager() {
        return getContext().getTransactionManager();
    }

    public SchemaManager getSchemaManager() {
        return getContext().getSchemaManager();
    }

    public void setDefaultContextName(String contextName) {
        if (!contexts.containsKey(contextName)) {
            throw new IllegalArgumentException("Context '" + contextName + "' does not exist");
        }

        this.defaultContextName = contextName;
    }

    public synchronized void shutdown() {
        contexts.values().forEach(context -> {
            try {
                context.close();
            } catch (Exception ex) {
                LOG.error("Error closing database context: " + ex.getMessage());
            }
        });

        contexts.clear();
    }

    public class DatabaseContext implements AutoCloseable {
        private final ConnectionManager connectionManager;
        private final TransactionManager transactionManager;
        private final SchemaManager schemaManager;

        public DatabaseContext(
                ConnectionManager connectionManager,
                TransactionManager transactionManager,
                SchemaManager schemaManager) {
            this.connectionManager = connectionManager;
            this.transactionManager = transactionManager;
            this.schemaManager = schemaManager;
        }

        public ConnectionManager getConnectionManager() {
            return connectionManager;
        }

        public TransactionManager getTransactionManager() {
            return transactionManager;
        }

        public SchemaManager getSchemaManager() {
            return schemaManager;
        }

        @Override
        public void close() throws Exception {
            connectionManager.close();
        }
    }

    private synchronized ConnectionManager initializeConnection(String path) throws Exception {
        ConnectionManager connectionManager = null;

        try {
            connectionManager = SQLiteConnectionProvider.create(path);
            connectionManager.initialize();
        } catch (Exception ex) {
            connectionManager = SQLiteConnectionProvider.create("metric-collector-core/" + path);
            connectionManager.initialize();
        }

        return connectionManager;
    }

    private synchronized void initializeSchema(SchemaManager schemaManager) throws Exception {
        schemaManager.initialize();

        List<String> sqlResourcePaths = findSQLResourcePaths(DIRECTORY_PATH);

        if (sqlResourcePaths.isEmpty()) {
            LOG.debug(String.format("No SQL files found in directory: %s", DIRECTORY_PATH));
            return;

        }

        for (String sqlResourcePath : sqlResourcePaths) {
            String sqlScript = readSQLScript(sqlResourcePath);
            executeSQLScript(schemaManager, sqlScript);
        }
    }

    private synchronized List<String> findSQLResourcePaths(String directoryPath) throws Exception {
        List<String> resources = new ArrayList<>();

        final String normalizedDirPath;

        if (directoryPath.startsWith("/")) {
            normalizedDirPath = directoryPath.substring(1);
        } else {
            normalizedDirPath = directoryPath;
        }

        URL dirURL = CURRENT_THREAD.getContextClassLoader().getResource(normalizedDirPath);

        if (dirURL == null) {
            LOG.debug(String.format("Directory not found in classpath: %s", normalizedDirPath));
            return resources;
        }

        if (dirURL.getProtocol().equals("file")) {
            resources = Files
                    .walk(Paths.get(dirURL.toURI()), 1)
                    .filter(Files::isRegularFile)
                    .filter(obj -> obj.toString().toLowerCase().endsWith(".sql"))
                    .map(obj -> normalizedDirPath + "/" + obj.getFileName().toString())
                    .collect(Collectors.toList());
        } else if (dirURL.getProtocol().equals("jar")) {
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));

            try (FileSystem fs = FileSystems.newFileSystem(URI.create("jar:file:" + jarPath), Collections.emptyMap())) {
                resources = Files
                        .walk(fs.getPath(directoryPath), 1)
                        .filter(Files::isRegularFile)
                        .filter(obj -> obj.toString().toLowerCase().endsWith(".sql"))
                        .map(obj -> {
                            String resourcePath = obj.toString();
                            return resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath;
                        })
                        .collect(Collectors.toList());
            }
        }

        return resources;
    }

    private synchronized String readSQLScript(String resourcePath) throws IOException {
        StringBuilder script = new StringBuilder();

        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);

        if (inputStream == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;

            while ((line = reader.readLine()) != null) {
                script.append(line).append("\n");
            }
        }

        return script.toString();
    }

    private synchronized void executeSQLScript(SchemaManager schemaManager, String sqlScript) {
        String[] statements = sqlScript.split(";");

        for (String sql : statements) {
            try {
                if (sql != null && !"".equalsIgnoreCase(sql.trim())) {
                    LOG.debug(String.format("Executing SQL: %s", sql));
                    schemaManager.execute(sql.trim());
                }
            } catch (SQLException ex) {
                LOG.debug(String.format("Failed to execute SQL: %s", sql), ex);
            }
        }
    }

}