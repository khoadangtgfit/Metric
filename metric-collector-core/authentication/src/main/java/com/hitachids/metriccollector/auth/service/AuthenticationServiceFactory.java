package com.hitachids.metriccollector.auth.service;

import com.hitachids.metriccollector.auth.model.TokenResponse;
import com.hitachids.metriccollector.auth.service.impl.HttpAuthenticationService;
import com.hitachids.metriccollector.storage.model.StorageModel;
import com.hitachids.metriccollector.storage.service.StorageService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Factory class for creating and managing AuthenticationService instances.
 */
public final class AuthenticationServiceFactory {
    private static final Log LOG = LogFactory.getLog(AuthenticationServiceFactory.class);
    private static final DateTimeFormatter LOG_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

    private AuthenticationServiceFactory() {
        throw new IllegalStateException("Factory class cannot be instantiated");
    }

    /**
     * Creates an HTTP-based AuthenticationService instance.
     *
     * @param storageService The storage service for retrieving storage details
     * @param storageId The ID of the storage system
     * @return A new AuthenticationService instance
     */
    public static AuthenticationService createHttpAuthService(StorageService storageService, int storageId) {
        if (storageService == null) {
            throw new IllegalArgumentException("StorageService cannot be null");
        }
        if (storageId <= 0) {
            throw new IllegalArgumentException("Invalid storage ID: " + storageId);
        }
        return new HttpAuthenticationService(storageService, storageId);
    }

    /**
     * Retrieves an authentication token, refreshing it if necessary.
     *
     * @param storageService The storage service for retrieving storage details
     * @param storageId The ID of the storage system
     * @return The authentication token prefixed with "Session ", or null if authentication fails
     */
    public static String getAuthToken(StorageService storageService, int storageId) {
        return getAuthToken(createHttpAuthService(storageService, storageId), storageService, storageId);
    }

    /**
     * Retrieves an authentication token using the provided AuthenticationService.
     *
     * @param authService The AuthenticationService instance to use
     * @param storageService The storage service for retrieving storage details
     * @param storageId The ID of the storage system
     * @return The authentication token prefixed with "Session ", or null if authentication fails
     */
    public static String getAuthToken(AuthenticationService authService, StorageService storageService, int storageId) {
        if (authService == null) {
            logWarn("Authentication service is null", storageService, storageId);
            return null;
        }

        String authToken = authService.getToken();

        if (authToken != null) {
            try {
                boolean isExpiredToken = !authService.validateToken();
                if (isExpiredToken) {
                    authToken = createNewToken(authService, storageService, storageId);
                }
            } catch (Exception ex) {
                logError("Failed to validate or refresh token", ex, storageService, storageId);
                return null;
            }
        } else {
            authToken = createNewToken(authService, storageService, storageId);
        }

        return authToken != null ? "Session " + authToken : null;
    }

    /**
     * Creates a new authentication token using the provided AuthenticationService.
     *
     * @param authService The AuthenticationService instance to use
     * @param storageService The storage service for retrieving storage details
     * @param storageId The ID of the storage system
     * @return The new token, or null if creation fails
     */
    public static String createNewToken(AuthenticationService authService, StorageService storageService, int storageId) {
        try {
            TokenResponse tokenResponse = authService.createTokenRequest(null);
            if (tokenResponse != null && tokenResponse.getToken() != null) {
                logInfo("Successfully created new token with session ID: " + tokenResponse.getSessionId(), storageService, storageId);
                return tokenResponse.getToken();
            }
            logWarn("Token response is null or contains no token", storageService, storageId);
            return null;
        } catch (Exception ex) {
            logError("Failed to create new token", ex, storageService, storageId);
            return null;
        }
    }

    public static String getMetricCollectorId(StorageService storageService, int storageId) {
        Optional<StorageModel> storageOpt = storageService.getStorage(storageId);
        if (storageOpt.isPresent()) {
            StorageModel storage = storageOpt.get();
            String storageIdStr = storage.getStorageId();
            String organizationId = storage.getOrganizationId();
            if (storageIdStr != null && organizationId != null) {
                return storageIdStr + "_" + organizationId;
            }
        }
        return "unknown_unknown";
    }

    public static void logInfo(String message, StorageService storageService, int storageId) {
        LOG.info(formatLog("INFO", getMetricCollectorId(storageService, storageId), message));
    }

    public static void logWarn(String message, StorageService storageService, int storageId) {
        LOG.warn(formatLog("WARN", getMetricCollectorId(storageService, storageId), message));
    }

    public static void logError(String message, Throwable ex, StorageService storageService, int storageId) {
        String logMessage = formatLog("ERROR", getMetricCollectorId(storageService, storageId), message + (ex != null ? ": " + ex.getMessage() : ""));
        LOG.error(logMessage, ex);
    }

    public static String formatLog(String level, String metricCollectorId, String message) {
        return String.format("%s [%s] %s Authentication: %s",
                ZonedDateTime.now().format(LOG_DATE_FORMAT),
                level,
                metricCollectorId,
                message);
    }
}