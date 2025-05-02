package com.hitachids.metriccollector.db.test;

import org.junit.jupiter.api.BeforeAll;

import com.hitachids.metriccollector.db.DatabaseManager;
import com.hitachids.metriccollector.db.schema.SchemaManager;

public abstract class DatabaseManagerTest {

	protected static DatabaseManager databaseManager;
	protected static SchemaManager schemaManager;

	@BeforeAll
	public static void setupDatabaseForTests() throws Exception {
		// Initialize database
		databaseManager = DatabaseManager.getInstance();
		databaseManager.initialize(":memory:");
	}

}