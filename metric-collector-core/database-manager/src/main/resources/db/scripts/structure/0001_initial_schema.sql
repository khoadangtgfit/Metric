CREATE TABLE IF NOT EXISTS "storage" (
	"id" INTEGER PRIMARY KEY AUTOINCREMENT,
	"storage_id" TEXT NOT NULL UNIQUE,
	"ipv4_service_ip" TEXT NOT NULL,
	"organization_id" TEXT NOT NULL,
	"credential_username" TEXT NOT NULL,
	"credential_password" TEXT NOT NULL,
	"created_at" TIMESTAMP NOT NULL,
	"created_by" TEXT NOT NULL,
	"updated_at" TIMESTAMP,
	"updated_by" TEXT
);

CREATE TABLE IF NOT EXISTS "metric_config" (
	"id" INTEGER PRIMARY KEY AUTOINCREMENT,
	"metric_type" TEXT NOT NULL UNIQUE,
	"interval" INTEGER NOT NULL,
	"granularity" TEXT,
	"is_enabled" INTEGER NOT NULL DEFAULT 1,
	"storage_id" INTEGER NOT NULL,
	"created_at" TIMESTAMP NOT NULL,
	"created_by" TEXT NOT NULL,
	"updated_at" TIMESTAMP,
	"updated_by" TEXT,
	FOREIGN KEY ("storage_id") REFERENCES "storage" ("id")
);