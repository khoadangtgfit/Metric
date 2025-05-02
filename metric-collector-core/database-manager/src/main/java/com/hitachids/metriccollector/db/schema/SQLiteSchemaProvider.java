package com.hitachids.metriccollector.db.schema;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.hitachids.metriccollector.db.connection.ConnectionManager;
import com.hitachids.metriccollector.db.transaction.SQLiteTransactionTemplate;
import com.hitachids.metriccollector.db.transaction.TransactionManager;

public class SQLiteSchemaProvider implements SchemaManager {

	private final TransactionManager transactionManager;

	public SQLiteSchemaProvider(ConnectionManager connectionManager) {
		this.transactionManager = new SQLiteTransactionTemplate(connectionManager);
	}

	@Override
	public void initialize() throws SQLException {
		transactionManager.executeInTransaction(connection -> {
			try (Statement stmt = connection.createStatement()) {
				stmt.execute("PRAGMA foreign_keys = ON");
			}

			return null;
		});
	}

	@Override
	public void execute(String sql) throws SQLException {
		transactionManager.executeInTransaction(connection -> {
			try (Statement stmt = connection.createStatement()) {
				stmt.execute(sql);
			}

			return null;
		});
	}

	@Override
	public <T> T executeQuery(String sql, ResultSetProcessor<T> processor) throws SQLException {
		return transactionManager.executeInTransaction(connection -> {
			try (
					Statement stmt = connection.createStatement();
					ResultSet rs = stmt.executeQuery(sql)) {
				return processor.process(rs);
			}
		});
	}

	@Override
	public <T> T executeQuery(
			String sql, PreparedStatementSetter paramSetter, ResultSetProcessor<T> processor)
			throws SQLException {

		return transactionManager.executeInTransaction(connection -> {
			try (PreparedStatement ps = connection.prepareStatement(sql)) {
				paramSetter.setValues(ps);

				try (ResultSet rs = ps.executeQuery()) {
					return processor.process(rs);
				}
			}
		});
	}

	@Override
	public <T> T executeInsert(String sql, ResultSetProcessor<T> processor) throws SQLException {
		return transactionManager.executeInTransaction(connection -> {
			try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				int affectedRows = ps.executeUpdate();

				if (affectedRows == 0) {
					throw new SQLException("Insert failed, no rows affected.");
				}

				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next()) {
						return processor.process(rs);
					}

					throw new SQLException("Insert failed, no ID obtained.");
				}
			}
		});
	}

	@Override
	public <T> T executeInsert(
			String sql, PreparedStatementSetter paramSetter, ResultSetProcessor<T> processor)
			throws SQLException {

		return transactionManager.executeInTransaction(connection -> {
			try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				paramSetter.setValues(ps);

				int affectedRows = ps.executeUpdate();

				if (affectedRows == 0) {
					throw new SQLException("Insert failed, no rows affected.");
				}

				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next()) {
						return processor.process(rs);
					}

					throw new SQLException("Insert failed, no ID obtained.");
				}
			}
		});
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {
		return transactionManager.executeInTransaction(connection -> {
			try (PreparedStatement ps = connection.prepareStatement(sql)) {
				return ps.executeUpdate();
			}
		});
	}

	@Override
	public int executeUpdate(String sql, PreparedStatementSetter paramSetter) throws SQLException {
		return transactionManager.executeInTransaction(connection -> {
			try (PreparedStatement ps = connection.prepareStatement(sql)) {
				paramSetter.setValues(ps);
				return ps.executeUpdate();
			}
		});
	}

	public static SQLiteSchemaProvider create(ConnectionManager connectionManager) {
		return new SQLiteSchemaProvider(connectionManager);
	}

}
