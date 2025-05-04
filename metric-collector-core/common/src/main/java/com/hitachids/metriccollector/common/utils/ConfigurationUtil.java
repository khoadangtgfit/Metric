package com.hitachids.metriccollector.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class for accessing configuration properties with default values
 * and type-safe retrieval methods.
 */
public final class ConfigurationUtil {
	private static final Log LOGGER = LogFactory.getLog(ConfigurationUtil.class);
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	// Configuration property keys
	private static final String DATABASE_PATH = "db.path";
	private static final String BASE_URL = "base.url";
	private static final String AUTH_TOKEN_SECRET = "auth.token.secret";
	private static final String API_REQUEST_TIMEOUT_MS = "api.request.timeout.milliseconds";
	private static final String COLLECTORS_BINARY_PATH = "collectors.binary.path";
	private static final String COLLECTOR_SCHEDULER_INTERVAL_SECONDS = "collector.scheduler.interval.seconds";
	private static final String SECURITY_ENCRYPTION_KEY = "security.encryption.key";
	private static final String MAX_RETRIES = "api.retry.max.attempts";
	private static final String MAX_BACKOFF_MS = "api.retry.max.backoff.milliseconds";

	// Default values as per documentation
	private static final int DEFAULT_API_TIMEOUT_MS = 1100; // 1.1 seconds
	private static final int DEFAULT_MAX_RETRIES = 5;
	private static final long DEFAULT_MAX_BACKOFF_MS = 10000L; // 10 seconds

	private ConfigurationUtil() {
		throw new IllegalStateException("Utility class cannot be instantiated");
	}

	/**
	 * Gets the database path configuration.
	 * @return Database path as string
	 */
	public static String getDatabasePath() {
		return PropertyUtil.getString(DATABASE_PATH);
	}

	/**
	 * Gets the base URL for API requests.
	 * @return Base URL as string
	 */
	public static String getBaseUrl() {
		return PropertyUtil.getString(BASE_URL);
	}

	/**
	 * Constructs a full API URL by appending the endpoint to the base URL.
	 * @param apiEndpoint The API endpoint to append
	 * @return Full API URL
	 */
	public static String getUrl(String apiEndpoint) {
		return getBaseUrl() + apiEndpoint;
	}

	/**
	 * Gets the authentication token with Basic prefix.
	 * @return Authentication token with Basic prefix
	 */
	public static String getAuthToken() {
		return "Basic " + PropertyUtil.getString(AUTH_TOKEN_SECRET);
	}

	/**
	 * Gets the API request timeout in milliseconds.
	 * @return Timeout in milliseconds
	 */
	public static int getTimeout() {
		return PropertyUtil.getInt(PropertyUtil.getDefaultConfigFile(), API_REQUEST_TIMEOUT_MS, DEFAULT_API_TIMEOUT_MS);
	}

	/**
	 * Gets the directory path for collector binaries.
	 * @return Collector binaries directory path
	 */
	public static String getCollectorBinariesDirectory() {
		return PropertyUtil.getString(COLLECTORS_BINARY_PATH);
	}

	/**
	 * Gets the collector scheduler interval in seconds.
	 * @return Scheduler interval in seconds
	 */
	public static int getCollectorSchedulerInterval() {
		return PropertyUtil.getInt(COLLECTOR_SCHEDULER_INTERVAL_SECONDS);
	}

	/**
	 * Gets the security encryption key.
	 * @return Encryption key
	 */
	public static String getSecurityEncryptionKey() {
		return PropertyUtil.getString(SECURITY_ENCRYPTION_KEY);
	}

	/**
	 * Gets the maximum number of retry attempts for API calls.
	 * @return Maximum retry attempts
	 */
	public static int getMaxRetries() {
		return PropertyUtil.getInt(PropertyUtil.getDefaultConfigFile(), MAX_RETRIES, DEFAULT_MAX_RETRIES);
	}

	/**
	 * Gets the maximum backoff time for retry attempts in milliseconds.
	 * @return Maximum backoff time in milliseconds
	 */
	public static long getMaxBackoffMs() {
		return PropertyUtil.getLong(PropertyUtil.getDefaultConfigFile(), MAX_BACKOFF_MS, DEFAULT_MAX_BACKOFF_MS);
	}

	/**
	 * Gets the logger instance for the metric collector package.
	 * @return Logger instance
	 */
	public static Log getLogger() {
		return LOGGER;
	}

	/**
	 * Gets the shared ObjectMapper instance.
	 * @return ObjectMapper instance
	 */
	public static ObjectMapper getObjectMapper() {
		return OBJECT_MAPPER;
	}
}