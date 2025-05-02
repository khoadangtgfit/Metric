package com.hitachids.metriccollector.connection.test;

import com.hitachids.metriccollector.db.connection.SQLiteConnectionProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class SQLiteConnectionProviderTest {

    private static final String DB_PATH = "metricscollector.db";
    private SQLiteConnectionProvider provider;

    @BeforeEach
    void setUp() {
        provider = SQLiteConnectionProvider.create(DB_PATH);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (provider != null) {
            provider.close();
        }

        File dbFile = new File(DB_PATH);
        if (dbFile.exists()) {
            dbFile.delete();
        }
    }

    @Test
    void testGetConnection_ShouldReturnValidConnection() throws SQLException {
        Connection conn = provider.getConnection();
        assertNotNull(conn);
        assertFalse(conn.isClosed());
    }

    @Test
    void testInitialize_ShouldEnableForeignKeysAndWALMode() throws SQLException {
        provider.initialize();
        Connection conn = provider.getConnection();

        var stmt = conn.createStatement();
        var rs = stmt.executeQuery("PRAGMA foreign_keys");
        assertTrue(rs.next());
        assertEquals(1, rs.getInt(1)); // foreign_keys should be ON

        rs = stmt.executeQuery("PRAGMA journal_mode");
        assertTrue(rs.next());
        assertEquals("wal", rs.getString(1).toLowerCase()); // journal_mode should be WAL
    }

    @Test
    void testClose_ShouldCloseConnection() throws Exception {
        Connection conn = provider.getConnection();
        assertFalse(conn.isClosed());

        provider.close();
        assertTrue(conn.isClosed());
    }

    @Test
    void testCreate_ShouldGenerateValidProvider() throws SQLException {
        SQLiteConnectionProvider createdProvider = SQLiteConnectionProvider.create(DB_PATH);
        assertNotNull(createdProvider.getConnection());
    }
}
