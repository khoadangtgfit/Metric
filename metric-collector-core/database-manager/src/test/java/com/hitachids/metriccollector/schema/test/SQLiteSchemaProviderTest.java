package com.hitachids.metriccollector.schema.test;

import com.hitachids.metriccollector.db.connection.ConnectionManager;
import com.hitachids.metriccollector.db.schema.SQLiteSchemaProvider;
import com.hitachids.metriccollector.db.schema.SchemaManager;
import com.hitachids.metriccollector.db.transaction.SQLiteTransactionTemplate;
import com.hitachids.metriccollector.db.transaction.TransactionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class SQLiteSchemaProviderTest {

    @Mock
    private Connection connection;
    @Mock
    private Statement statement;
    @Mock
    private PreparedStatement preparedStatement;
    @Mock
    private ResultSet resultSet;
    @Mock
    private SchemaManager.ResultSetProcessor<String> resultSetProcessor;
    @Mock
    private SchemaManager.PreparedStatementSetter paramSetter;

    private SQLiteSchemaProvider provider;
    private TransactionManager transactionManager;

    @BeforeEach
    void setUp() throws SQLException {
        MockitoAnnotations.openMocks(this);
        ConnectionManager connectionManager = mock(ConnectionManager.class);
        when(connectionManager.getConnection()).thenReturn(connection);

        transactionManager = new SQLiteTransactionTemplate(connectionManager);
        provider = new SQLiteSchemaProvider(connectionManager);
    }

    @Test
    void testInitialize() throws Exception {
        when(connection.createStatement()).thenReturn(statement);
        provider.initialize();
        verify(statement).execute("PRAGMA foreign_keys = ON");
    }

    @Test
    void testExecute() throws Exception {
        String sql = "CREATE TABLE test (id INTEGER)";
        when(connection.createStatement()).thenReturn(statement);
        provider.execute(sql);
        verify(statement).execute(sql);
    }

    @Test
    void testExecuteQueryWithoutParams() throws Exception {
        String sql = "SELECT * FROM test";
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery(sql)).thenReturn(resultSet);
        when(resultSetProcessor.process(resultSet)).thenReturn("result");

        String result = provider.executeQuery(sql, resultSetProcessor);
        assertEquals("result", result);
    }

    @Test
    void testExecuteQueryWithParams() throws Exception {
        String sql = "SELECT * FROM test WHERE id = ?";
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
        when(preparedStatement.executeQuery()).thenReturn(resultSet);
        when(resultSetProcessor.process(resultSet)).thenReturn("result");

        String result = provider.executeQuery(sql, paramSetter, resultSetProcessor);
        assertEquals("result", result);
    }

    @Test
    void testExecuteInsertWithoutParams() throws Exception {
        String sql = "INSERT INTO test (name) VALUES ('name')";
        when(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSetProcessor.process(resultSet)).thenReturn("inserted");

        String result = provider.executeInsert(sql, resultSetProcessor);
        assertEquals("inserted", result);
    }

    @Test
    void testExecuteInsertWithParams() throws Exception {
        String sql = "INSERT INTO test (name) VALUES (?)";
        when(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(1);
        when(preparedStatement.getGeneratedKeys()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSetProcessor.process(resultSet)).thenReturn("inserted");

        String result = provider.executeInsert(sql, paramSetter, resultSetProcessor);
        assertEquals("inserted", result);
    }

    @Test
    void testExecuteUpdateWithoutParams() throws Exception {
        String sql = "UPDATE test SET name = 'updated'";
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(2);

        int result = provider.executeUpdate(sql);
        assertEquals(2, result);
    }

    @Test
    void testExecuteUpdateWithParams() throws Exception {
        String sql = "UPDATE test SET name = ? WHERE id = ?";
        when(connection.prepareStatement(sql)).thenReturn(preparedStatement);
        when(preparedStatement.executeUpdate()).thenReturn(2);

        int result = provider.executeUpdate(sql, paramSetter);
        assertEquals(2, result);
    }
}
