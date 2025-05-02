package com.hitachids.metriccollector.db.transaction;

import java.sql.Connection;
import java.sql.SQLException;

import com.hitachids.metriccollector.db.connection.ConnectionManager;

public class SQLiteTransactionTemplate implements TransactionManager {

	private final ConnectionManager connectionManager;

	public SQLiteTransactionTemplate(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	@Override
	public <T> T executeInTransaction(TransactionCallback<T> callback) throws SQLException {
		Connection connection = connectionManager.getConnection();

		boolean originalAutoCommit = connection.getAutoCommit();

		try {
			if (originalAutoCommit) {
				connection.setAutoCommit(false);
			}

			T result = callback.execute(connection);

			connection.commit();

			return result;
		} catch (SQLException ex) {
			connection.rollback();
			throw ex;
		} finally {
			if (originalAutoCommit) {
				connection.setAutoCommit(true);
			}
		}
	}

}
