package com.hitachids.metriccollector.db.connection;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionManager extends AutoCloseable {

	public Connection getConnection() throws SQLException;

	public void initialize() throws SQLException;

	@Override
	public void close() throws Exception;

}