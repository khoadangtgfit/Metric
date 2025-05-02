package com.hitachids.metriccollector.db.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class SQLiteConnectionProvider implements ConnectionManager {

	private Connection connection;
	private final String dbUrl;
	private final Properties connectionProps;

	public SQLiteConnectionProvider(String dbUrl) {
		this.dbUrl = dbUrl;
		this.connectionProps = new Properties();
	}

	public SQLiteConnectionProvider(String dbUrl, Properties props) {
		this.dbUrl = dbUrl;
		this.connectionProps = props;
	}

	@Override
	public Connection getConnection() throws SQLException {
		if (connection == null || connection.isClosed()) {
			initialize();
		}

		return connection;
	}

	@Override
	public void initialize() throws SQLException {
		if (connection == null || connection.isClosed()) {
			connection = DriverManager.getConnection(dbUrl, connectionProps);

			try (Statement stmt = connection.createStatement()) {
				// Enable foreign keys
				stmt.execute("PRAGMA foreign_keys = ON");
				// Set journal mode to WAL for better concurrency
				stmt.execute("PRAGMA journal_mode = WAL");
				// Set synchronous mode
				stmt.execute("PRAGMA synchronous = NORMAL");
			}
		}
	}

	@Override
	public void close() throws Exception {
		if (connection == null || connection.isClosed()) {
			return;
		}

		connection.close();
		connection = null;
	}

	public static SQLiteConnectionProvider create(String path) {
		return new SQLiteConnectionProvider("jdbc:sqlite:" + path);
	}

}