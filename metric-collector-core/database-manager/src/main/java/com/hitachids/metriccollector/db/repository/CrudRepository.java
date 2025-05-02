package com.hitachids.metriccollector.db.repository;

import java.sql.SQLException;
import java.util.List;

import com.hitachids.metriccollector.db.schema.SchemaManager.PreparedStatementSetter;
import com.hitachids.metriccollector.db.schema.SchemaManager.ResultSetProcessor;

public interface CrudRepository<K, T> {

	public List<K> fetchEntities(String sql, ResultSetProcessor<List<K>> processor) throws SQLException;

	public List<K> fetchEntities(
			String sql, PreparedStatementSetter paramSetter, ResultSetProcessor<List<K>> processor)
			throws SQLException;

	public K fetchEntity(String sql, ResultSetProcessor<K> processor) throws SQLException;

	public K fetchEntity(
			String sql, PreparedStatementSetter paramSetter, ResultSetProcessor<K> processor)
			throws SQLException;

	public K insertEntity(
			String sql, PreparedStatementSetter paramSetter, ResultSetProcessor<K> processor)
			throws SQLException;

	public int updateEntity(String sql, PreparedStatementSetter paramSetter) throws SQLException;

	public int deleteEntity(String sql, PreparedStatementSetter paramSetter) throws SQLException;

}