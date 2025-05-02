package com.hitachids.metriccollector.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigurationUtil {
	private static final String DATABASE_PATH = "db.path";
	private static final String BASE_URL = "base.url";
	private static final String AUTH_TOKEN_SECRET = "auth.token.secret";
	private static final String API_REQUEST_TIMEOUT_SECONDS = "api.request.timeout.milliseconds";
	private static final String COLLECTORS_BINARY_PATH = "collectors.binary.path";
	private static final String COLLECTOR_SCHEDULER_INTERVAL_SECONDS = "collector.scheduler.interval.seconds";
	private static final String SECURITY_ENCRYPTION_KEY = "security.encryption.key";
	private static final String MAX_RETRIES = "api.retry.max.attempts";
	private static final String MAX_BACKOFF_MS = "api.retry.max.backoff.milliseconds";
	private static final Log LOGGER = LogFactory.getLog(ConfigurationUtil.class);
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private ConfigurationUtil() {
		throw new IllegalStateException("Utility class");
	}

	public static String getDatabasePath() {
		return getDatabasePath(DATABASE_PATH);
	}

	public static String getDatabasePath(String dbPath) {
		return PropertyUtil.getString(dbPath);
	}

	public static String getBaseUrl() {
		return getBaseUrl(BASE_URL);
	}

	public static String getBaseUrl(String baseUrl) {
		return PropertyUtil.getString(baseUrl);
	}

	public static String getUrl(String apiEndPoint) {
		return getUrl(BASE_URL, apiEndPoint);
	}

	public static String getUrl(String baseUrl, String apiEndPoint) {
		return PropertyUtil.getString(baseUrl) + apiEndPoint;
	}

	public static String getAuthToken() {
		return getAuthToken(AUTH_TOKEN_SECRET);
	}

	public static String getAuthToken(String authTokenSecret) {
		return "Basic " + PropertyUtil.getString(authTokenSecret);
	}

	public static Integer getTimeout() {
		return getTimeout(API_REQUEST_TIMEOUT_SECONDS);
	}

	public static Integer getTimeout(String timeout) {
		// Default to 1100ms (1.1s) as per document
		return PropertyUtil.getInt(timeout, String.valueOf(1100));
	}

	public static String getCollectorBinariesDirectory() {
		return getCollectorBinariesDirectory(COLLECTORS_BINARY_PATH);
	}

	public static String getCollectorBinariesDirectory(String collectorsBinaryPath) {
		return PropertyUtil.getString(collectorsBinaryPath);
	}

	public static Integer getCollectorSchedulerInterval() {
		return getCollectorSchedulerInterval(COLLECTOR_SCHEDULER_INTERVAL_SECONDS);
	}

	public static Integer getCollectorSchedulerInterval(String collectorSchedulerIntervalSeconds) {
		return PropertyUtil.getInt(collectorSchedulerIntervalSeconds);
	}

	public static String getSecurityEncryptionKey() {
		return getSecurityEncryptionKey(SECURITY_ENCRYPTION_KEY);
	}

	public static String getSecurityEncryptionKey(String securityEncryptionKey) {
		return PropertyUtil.getString(securityEncryptionKey);
	}

	public static Integer getMaxRetries() {
		return getMaxRetries(MAX_RETRIES);
	}

	public static Integer getMaxRetries(String maxRetries) {
		// Default to 5 as per document
		return PropertyUtil.getInt(maxRetries, String.valueOf(5));
	}

	public static Integer getMaxRetries(int defaultValue) {
		return PropertyUtil.getInt(MAX_RETRIES, String.valueOf(defaultValue));
	}

	public static Long getMaxBackoffMs() {
		return getMaxBackoffMs(MAX_BACKOFF_MS);
	}

	public static Long getMaxBackoffMs(String maxBackoffMs) {
		// Default to 10000ms (10s)
		return PropertyUtil.getLong(maxBackoffMs, String.valueOf(10000L));
	}

	public static Log getLogger() {
		return LogFactory.getLog("com.hitachids.metriccollector");
	}

	public static ObjectMapper getObjectMapper() {
		return OBJECT_MAPPER;
	}
}