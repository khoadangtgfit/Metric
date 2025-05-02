package com.hitachids.metriccollector.database.test;

import com.hitachids.metriccollector.db.DatabaseManager;
import com.hitachids.metriccollector.db.connection.ConnectionManager;
import com.hitachids.metriccollector.db.schema.SchemaManager;
import com.hitachids.metriccollector.db.transaction.TransactionManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DatabaseManagerTest {

    @Mock
    ConnectionManager mockConnectionManager;
    @Mock
    TransactionManager mockTransactionManager;
    @Mock
    SchemaManager mockSchemaManager;
    @Mock
    Connection mockConnection;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(mockConnectionManager.getConnection()).thenReturn(mockConnection);
    }

    @Test
    void testSingletonInstance() {
        DatabaseManager instance1 = DatabaseManager.getInstance();
        DatabaseManager instance2 = DatabaseManager.getInstance();
        assertSame(instance1, instance2);
    }

    @Test
    void testSetDefaultContextNameThrowsIfNotExist() {
        DatabaseManager manager = DatabaseManager.getInstance();
        assertThrows(IllegalArgumentException.class, () -> manager.setDefaultContextName("invalid"));
    }

    @Test
    void testGetContextThrowsIfNotInitialized() {
        DatabaseManager manager = DatabaseManager.getInstance();
        assertThrows(IllegalStateException.class, () -> manager.getContext("unknown"));
    }

    @Test
    void testInitializeAndGetContext() throws Exception {
        DatabaseManager manager = DatabaseManager.getInstance();

        // Reflection or resetting state might be required for repeated tests
        String testContext = "test-context";

        DatabaseManager.DatabaseContext context = manager.new DatabaseContext(
                mockConnectionManager, mockTransactionManager, mockSchemaManager
        );

        // Inject manually for test
        var contextsField = DatabaseManager.class.getDeclaredField("contexts");
        contextsField.setAccessible(true);
        ((Map<String, DatabaseManager.DatabaseContext>) contextsField.get(manager)).put(testContext, context);

        assertEquals(context, manager.getContext(testContext));
        assertEquals(mockConnectionManager, manager.getContext(testContext).getConnectionManager());
        assertEquals(mockTransactionManager, manager.getContext(testContext).getTransactionManager());
        assertEquals(mockSchemaManager, manager.getContext(testContext).getSchemaManager());
    }

    @Test
    void testGetConnectionFromContext() throws Exception {
        DatabaseManager manager = DatabaseManager.getInstance();
        String testContext = "test-context-conn";
        DatabaseManager.DatabaseContext context = manager.new DatabaseContext(
                mockConnectionManager, mockTransactionManager, mockSchemaManager
        );

        var contextsField = DatabaseManager.class.getDeclaredField("contexts");
        contextsField.setAccessible(true);
        ((Map<String, DatabaseManager.DatabaseContext>) contextsField.get(manager)).put(testContext, context);

        assertEquals(mockConnection, manager.getConnection(testContext));
    }

    @Test
    void testShutdownClosesAllContexts() throws Exception {
        DatabaseManager manager = DatabaseManager.getInstance();
        DatabaseManager.DatabaseContext context = spy(manager.new DatabaseContext(
                mockConnectionManager, mockTransactionManager, mockSchemaManager
        ));

        doNothing().when(mockConnectionManager).close();

        var contextsField = DatabaseManager.class.getDeclaredField("contexts");
        contextsField.setAccessible(true);
        Map<String, DatabaseManager.DatabaseContext> map = new HashMap<>();
        map.put("default", context);
        contextsField.set(manager, map);

        manager.shutdown();
        verify(mockConnectionManager, atLeastOnce()).close();
    }

}
