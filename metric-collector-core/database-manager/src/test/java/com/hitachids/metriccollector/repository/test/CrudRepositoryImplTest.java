package com.hitachids.metriccollector.repository.test;

import com.hitachids.metriccollector.db.DatabaseManager;
import com.hitachids.metriccollector.db.repository.impl.CrudRepositoryImpl;
import com.hitachids.metriccollector.db.schema.SchemaManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CrudRepositoryImplTest {
    private SchemaManager schemaManager;
    private CrudRepositoryImpl<Integer, String> repository;
    private MockedStatic<DatabaseManager> dbManagerStatic;

    static class TestRepository extends CrudRepositoryImpl<Integer, String> {
    }

    @BeforeEach
    void setUp() {
        schemaManager = mock(SchemaManager.class);
        repository = new TestRepository();

        DatabaseManager mockDbManager = mock(DatabaseManager.class);
        when(mockDbManager.getSchemaManager()).thenReturn(schemaManager);

        dbManagerStatic = mockStatic(DatabaseManager.class);
        dbManagerStatic.when(DatabaseManager::getInstance).thenReturn(mockDbManager);
    }

    @AfterEach
    void tearDown() {
        dbManagerStatic.close();
    }

    @Test
    void testFetchEntities_WithProcessor() throws SQLException {
        String sql = "SELECT * FROM users";
        List<Integer> expected = List.of(1, 2, 3);

        @SuppressWarnings("unchecked")
        SchemaManager.ResultSetProcessor<List<Integer>> processor = mock(SchemaManager.ResultSetProcessor.class);
        when(schemaManager.executeQuery(sql, processor)).thenReturn(expected);

        List<Integer> result = repository.fetchEntities(sql, processor);

        assertEquals(expected, result);
        verify(schemaManager).executeQuery(sql, processor);
    }

    @Test
    void testInsertEntity_ReturnsGeneratedKey() throws SQLException {
        String sql = "INSERT INTO users (id) VALUES (?)";
        Integer expected = 1;

        SchemaManager.PreparedStatementSetter setter = mock(SchemaManager.PreparedStatementSetter.class);
        @SuppressWarnings("unchecked")
        SchemaManager.ResultSetProcessor<Integer> processor = mock(SchemaManager.ResultSetProcessor.class);
        when(schemaManager.executeInsert(sql, setter, processor)).thenReturn(expected);

        Integer result = repository.insertEntity(sql, setter, processor);

        assertEquals(expected, result);
        verify(schemaManager).executeInsert(sql, setter, processor);
    }

    @Test
    void testDeleteEntity_ReturnsAffectedRowCount() throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        int expected = 1;

        SchemaManager.PreparedStatementSetter paramSetter = mock(SchemaManager.PreparedStatementSetter.class);
        when(schemaManager.executeUpdate(sql, paramSetter)).thenReturn(expected);

        int result = repository.deleteEntity(sql, paramSetter);

        assertEquals(expected, result);
        verify(schemaManager).executeUpdate(sql, paramSetter);
    }

    @Test
    void testUpdateEntity_ThenSuccess() throws SQLException {
        String sql = "UPDATE users set name = ? WHERE id = ?";
        int expected = 1;

        SchemaManager.PreparedStatementSetter paramSetter = mock(SchemaManager.PreparedStatementSetter.class);
        when(schemaManager.executeUpdate(sql, paramSetter)).thenReturn(expected);

        int result = repository.updateEntity(sql, paramSetter);
        assertEquals(expected, result);
        verify(schemaManager).executeUpdate(sql, paramSetter);
    }
}
