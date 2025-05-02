package com.hitachids.metriccollector.transaction.test;

import com.hitachids.metriccollector.db.connection.ConnectionManager;
import com.hitachids.metriccollector.db.transaction.SQLiteTransactionTemplate;
import com.hitachids.metriccollector.db.transaction.TransactionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SQLiteTransactionTemplateTest {

    private ConnectionManager connectionManager;
    private Connection connection;
    private TransactionManager transactionManager;

    @BeforeEach
    void setUp() throws SQLException {
        connectionManager = mock(ConnectionManager.class);
        connection = mock(Connection.class);
        when(connectionManager.getConnection()).thenReturn(connection);
        transactionManager = new SQLiteTransactionTemplate(connectionManager);
    }

    @Test
    void testExecuteInTransactionSuccessWithAutoCommitTrue() throws SQLException {
        when(connection.getAutoCommit()).thenReturn(true);

        String result = transactionManager.executeInTransaction(conn -> {
            assertSame(connection, conn);
            return "SUCCESS";
        });

        assertEquals("SUCCESS", result);
        verify(connection).setAutoCommit(false);
        verify(connection).commit();
        verify(connection).setAutoCommit(true);

    }

    @Test
    void testExecuteInTransactionSuccessWithAutoCommitFalse() throws SQLException {
        when(connection.getAutoCommit()).thenReturn(false);

        String result = transactionManager.executeInTransaction(conn -> "SUCCESS");

        assertEquals("SUCCESS", result);
        verify(connection, never()).setAutoCommit(false);
        verify(connection).commit();
        verify(connection, never()).setAutoCommit(true);

    }

    @Test
    void testExecuteInTransactionRollbackOnSQLException() throws SQLException {
        when(connection.getAutoCommit()).thenReturn(true);

        SQLException sqlException = new SQLException("DB Error");

        // WHEN
        TransactionManager.TransactionCallback<String> callback = conn -> {
            throw sqlException;
        };

        // THEN
        SQLException thrown = assertThrows(SQLException.class, () -> transactionManager.executeInTransaction(callback));

        assertSame(sqlException, thrown);

        // Verify the rollback behavior and never commit
        verify(connection).setAutoCommit(false);
        verify(connection, never()).commit();
        verify(connection).rollback();
        verify(connection).setAutoCommit(true);
    }

}
