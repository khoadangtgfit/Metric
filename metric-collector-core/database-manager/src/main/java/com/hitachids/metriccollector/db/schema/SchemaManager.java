package com.hitachids.metriccollector.db.schema;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface SchemaManager {

	public void initialize() throws SQLException;

	public void execute(String sql) throws SQLException;

	public <T> T executeQuery(String sql, ResultSetProcessor<T> processor) throws SQLException;

	public <T> T executeQuery(
			String sql, PreparedStatementSetter paramSetter, ResultSetProcessor<T> processor)
			throws SQLException;

	public <T> T executeInsert(String sql, ResultSetProcessor<T> processor) throws SQLException;

	public <T> T executeInsert(
			String sql, PreparedStatementSetter paramSetter, ResultSetProcessor<T> processor)
			throws SQLException;

	public int executeUpdate(String sql) throws SQLException;

	public int executeUpdate(String sql, PreparedStatementSetter paramSetter) throws SQLException;

	@FunctionalInterface
	interface ResultSetProcessor<T> {
		T process(ResultSet resultSet) throws SQLException;
	}

	@FunctionalInterface
	interface PreparedStatementSetter {
		void setValues(PreparedStatement ps) throws SQLException;
	}

}
