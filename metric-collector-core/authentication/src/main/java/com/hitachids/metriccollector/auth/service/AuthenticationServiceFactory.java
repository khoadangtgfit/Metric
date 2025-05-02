package com.hitachids.metriccollector.auth.service;

import com.hitachids.metriccollector.auth.model.TokenResponse;
import com.hitachids.metriccollector.auth.service.impl.HttpAuthenticationService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class AuthenticationServiceFactory {
	private static final Log LOG = LogFactory.getLog(AuthenticationServiceFactory.class);
	private static final DateTimeFormatter LOG_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

	private AuthenticationServiceFactory() {
	}

	public static AuthenticationService createHttpAuthService(int storageId) {
		try {
			return new HttpAuthenticationService(storageId);
		} catch (Exception ex) {
			logError("Failed to create authentication service for storage ID: " + storageId, ex, storageId);
			return null;
		}
	}

	public static String getAuthToken(int storageId, String userId, String password) {
		AuthenticationService authService = createHttpAuthService(storageId);
		if (authService == null) {
			logError("Authentication service creation failed for storage ID: " + storageId, null, storageId);
			return null;
		}
		return getAuthToken(authService, userId, password);
	}

	public static String getAuthToken(AuthenticationService authService, String userId, String password) {
		if (authService == null) {
			LOG.warn("Authentication service is null");
			return null;
		}

		String authToken = authService.getToken();
		if (authToken != null) {
			try {
				boolean isExpiredToken = !authService.validateToken();
				if (isExpiredToken) {
					authToken = createNewToken(authService, userId, password);
				}
			} catch (Exception ex) {
				logError("Failed to validate or refresh token", ex, 0);
			}
		} else {
			authToken = createNewToken(authService, userId, password);
		}
		return authToken != null ? "Session ".concat(authToken) : null;
	}

	private static String createNewToken(AuthenticationService authService, String userId, String password) {
		try {
			TokenResponse tokenResponse = authService.createTokenRequest(userId, password);
			if (tokenResponse != null) {
				return tokenResponse.getToken();
			}
		} catch (Exception ex) {
			logError("Failed to create new token", ex, 0);
		}
		return null;
	}

	private static void logError(String message, Throwable ex, int storageId) {
		String metricCollectorId = "storage_" + storageId + "_org";
		String logMessage = String.format("%s [ERROR] %s Authentication: %s",
				ZonedDateTime.now().format(LOG_DATE_FORMAT),
				metricCollectorId,
				message + (ex != null ? ": " + ex.getMessage() : ""));
		LOG.error(logMessage, ex);
	}
}