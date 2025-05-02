package com.hitachids.metriccollector.auth.service.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hitachids.metriccollector.auth.model.TokenResponse;
import com.hitachids.metriccollector.auth.service.AuthenticationService;
import com.hitachids.metriccollector.common.constant.ApiConstants;
import com.hitachids.metriccollector.common.utils.ApiUtil;
import com.hitachids.metriccollector.common.utils.ConfigurationUtil;
import com.hitachids.metriccollector.resiliencer.Resiliencer;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.regex.Pattern;

public class HttpAuthenticationService implements AuthenticationService {
	private static final DateTimeFormatter LOG_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
	private static final Pattern USER_ID_PATTERN = Pattern.compile("^[A-Za-z0-9!#$%&'*+\\-./=?@^_`{|}~]+$");
	private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[\\x21-\\x7E]+$");

	private String cachedToken = null;
	private String cachedSessionId = null;
	private final int storageId;
	private final String metricCollectorId;
	private final Resiliencer resiliencer;

	public HttpAuthenticationService(int storageId) throws Exception {
		this.storageId = storageId;
		this.metricCollectorId = initializeMetricCollectorId();
		this.resiliencer = new Resiliencer();
	}

	private String initializeMetricCollectorId() throws Exception {
		return "storage_" + storageId + "_org";
	}

	@Override
	public TokenResponse createTokenRequest(String userId, String password) throws Exception {
		// Validate userId and password format
		if (userId == null || userId.isEmpty()) {
			String message = "User ID cannot be null or empty";
			logError(message, null);
			throw new IllegalArgumentException(message);
		}
		if (password == null || password.isEmpty()) {
			String message = "Password cannot be null or empty";
			logError(message, null);
			throw new IllegalArgumentException(message);
		}
		if (!USER_ID_PATTERN.matcher(userId).matches()) {
			String message = "Invalid user ID format: contains disallowed characters";
			logError(message, null);
			throw new IllegalArgumentException(message);
		}
		if (!PASSWORD_PATTERN.matcher(password).matches()) {
			String message = "Invalid password format: contains disallowed characters or whitespace";
			logError(message, null);
			throw new IllegalArgumentException(message);
		}

		String authHeader = createBasicAuthHeader(userId, password);

		return resiliencer.executeWithRetry(() -> {
			try {
				ObjectNode responseNode = ApiUtil.post(
						ApiUtil.getURI(ApiConstants.SESSIONS_ENDPOINT).toString(),
						null, // No request body per document
						authHeader,
						ConfigurationUtil.getTimeout()
				);
				TokenResponse tokenResponse = ConfigurationUtil.getObjectMapper().treeToValue(responseNode, TokenResponse.class);
				this.cachedToken = tokenResponse.getToken();
				this.cachedSessionId = tokenResponse.getSessionId();
				logInfo("Successfully created token with session ID: " + this.cachedSessionId);
				return tokenResponse;
			} catch (Exception ex) {
				this.cachedToken = null;
				this.cachedSessionId = null;
				String message = "Failed to create token for user ID: " + userId;
				logError(message, ex);
				throw ex;
			}
		});
	}

	@Override
	public boolean validateToken() throws Exception {
		if (this.cachedSessionId == null || this.cachedToken == null) {
			logWarn("No session ID or token available for validation");
			return false;
		}

		return resiliencer.executeWithRetry(() -> {
			try {
				ObjectNode responseNode = ApiUtil.get(
						ApiUtil.getURI(ApiConstants.SESSIONS_ENDPOINT + "/" + this.cachedSessionId).toString(),
						"Session " + this.cachedToken,
						ConfigurationUtil.getTimeout()
				);
				boolean isValid = responseNode != null;
				if (!isValid) {
					this.cachedToken = null;
					this.cachedSessionId = null;
					logWarn("Token validation failed for session ID: " + this.cachedSessionId);
				}
				return isValid;
			} catch (Exception ex) {
				this.cachedToken = null;
				this.cachedSessionId = null;
				String message = "Failed to validate token for session ID: " + this.cachedSessionId;
				logError(message, ex);
				throw ex;
			}
		});
	}

	@Override
	public String getToken() {
		return this.cachedToken;
	}

	private String createBasicAuthHeader(String userId, String password) {
		String credentials = userId + ":" + password;
		String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
		return "Basic " + encodedCredentials;
	}

	private void logInfo(String message) {
		ConfigurationUtil.getLogger().info(formatLog("INFO", message));
	}

	private void logWarn(String message) {
		ConfigurationUtil.getLogger().warn(formatLog("WARN", message));
	}

	private void logError(String message, Throwable ex) {
		String logMessage = formatLog("ERROR", message + (ex != null ? ": " + ex.getMessage() : ""));
		ConfigurationUtil.getLogger().error(logMessage, ex);
	}

	private String formatLog(String level, String message) {
		return String.format("%s [%s] %s Authentication: %s",
				ZonedDateTime.now().format(LOG_DATE_FORMAT),
				level,
				metricCollectorId,
				message);
	}
}