package com.hitachids.metriccollector.db.repository.impl;

import java.sql.SQLException;
import java.util.List;

import com.hitachids.metriccollector.db.DatabaseManager;
import com.hitachids.metriccollector.db.repository.CrudRepository;
import com.hitachids.metriccollector.db.schema.SchemaManager;
import com.hitachids.metriccollector.db.schema.SchemaManager.PreparedStatementSetter;
import com.hitachids.metriccollector.db.schema.SchemaManager.ResultSetProcessor;

public abstract class CrudRepositoryImpl<K, T> implements CrudRepository<K, T> {

	private SchemaManager getSchemaManager() {
		return DatabaseManager.getInstance().getSchemaManager();
	}

	@Override
	public List<K> fetchEntities(String sql, ResultSetProcessor<List<K>> processor) throws SQLException {
		return getSchemaManager().executeQuery(sql, processor);
	}

	@Override
	public List<K> fetchEntities(
			String sql, PreparedStatementSetter paramSetter, ResultSetProcessor<List<K>> processor)
			throws SQLException {
		return getSchemaManager().executeQuery(sql, paramSetter, processor);
	}

	@Override
	public K fetchEntity(String sql, ResultSetProcessor<K> processor) throws SQLException {
		return getSchemaManager().executeQuery(sql, processor);
	}

	@Override
	public K fetchEntity(
			String sql, PreparedStatementSetter paramSetter, ResultSetProcessor<K> processor)
			throws SQLException {
		return getSchemaManager().executeQuery(sql, paramSetter, processor);
	}

	@Override
	public K insertEntity(
			String sql, PreparedStatementSetter paramSetter, ResultSetProcessor<K> processor)
			throws SQLException {
		return getSchemaManager().executeInsert(sql, paramSetter, processor);
	}

	@Override
	public int updateEntity(String sql, PreparedStatementSetter paramSetter) throws SQLException {
		return getSchemaManager().executeUpdate(sql, paramSetter);
	}

	@Override
	public int deleteEntity(String sql, PreparedStatementSetter paramSetter) throws SQLException {
		return getSchemaManager().executeUpdate(sql, paramSetter);
	}

}