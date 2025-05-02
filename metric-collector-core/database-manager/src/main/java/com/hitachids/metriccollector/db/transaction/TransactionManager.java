package com.hitachids.metriccollector.db.transaction;

import java.sql.Connection;
import java.sql.SQLException;

public interface TransactionManager {

	public <T> T executeInTransaction(TransactionCallback<T> callback) throws SQLException;

	@FunctionalInterface
	interface TransactionCallback<T> {
		T execute(Connection connection) throws SQLException;
	}

}
