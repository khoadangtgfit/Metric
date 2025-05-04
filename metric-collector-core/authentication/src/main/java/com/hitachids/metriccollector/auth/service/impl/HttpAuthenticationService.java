package com.hitachids.metriccollector.auth.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hitachids.metriccollector.auth.model.TokenRequest;
import com.hitachids.metriccollector.auth.model.TokenResponse;
import com.hitachids.metriccollector.auth.service.AuthenticationService;
import com.hitachids.metriccollector.common.constant.ApiConstants;
import com.hitachids.metriccollector.common.security.CredentialEncryptor;
import com.hitachids.metriccollector.common.utils.ApiUtil;
import com.hitachids.metriccollector.common.utils.ConfigurationUtil;
import com.hitachids.metriccollector.resiliencer.Resiliencer;
import com.hitachids.metriccollector.storage.model.StorageModel;
import com.hitachids.metriccollector.storage.service.StorageService;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Implementation of AuthenticationService for HTTP-based authentication with the Salamander API.
 */
public class HttpAuthenticationService implements AuthenticationService {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final DateTimeFormatter LOG_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
    private static final int MAX_RETRIES = ConfigurationUtil.getMaxRetries();

    // Regular expressions for User ID and Password validation
    private static final Pattern USER_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9!#$%&'*+\\-./=?@^_`{|}~]*$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^[\\p{ASCII}&&[^\\s]]*$");

    private String cachedToken = null;
    private Integer cachedSessionId = null;
    private final StorageService storageService;
    private final CredentialEncryptor credentialEncryptor;
    private final Resiliencer resiliencer;
    private final int storageId;

    public HttpAuthenticationService(StorageService storageService, int storageId) {
        this.storageService = storageService;
        this.credentialEncryptor = new CredentialEncryptor();
        this.resiliencer = new Resiliencer(MAX_RETRIES);
        this.storageId = storageId;
    }

    @Override
    public TokenResponse createTokenRequest(TokenRequest tokenRequest) throws Exception {
        Optional<StorageModel> storageOpt = storageService.getStorage(storageId);
        if (!storageOpt.isPresent()) {
            throw new IllegalStateException("Storage not found for ID: " + storageId);
        }

        StorageModel storage = storageOpt.get();
        String userId = storage.getUserId();
        String password = getDecryptedPassword(storage);

        // Validate User ID and Password formats
        validateCredentials(userId, password);

        String authHeader = createBasicAuthHeader(userId, password);
        String requestBody = getRequestBody(tokenRequest);
        return resiliencer.executeWithRetry(() -> {
            try {
                ObjectNode responseNode = ApiUtil.post(
                        ApiUtil.getURI(ApiConstants.SESSIONS_ENDPOINT),
                        requestBody,
                        authHeader,
                        ConfigurationUtil.getTimeout()
                );
                TokenResponse tokenResponse = OBJECT_MAPPER.treeToValue(responseNode, TokenResponse.class);
                this.cachedToken = tokenResponse.getToken();
                this.cachedSessionId = Integer.valueOf(tokenResponse.getSessionId());
                logInfo("Successfully created token with session ID: " + this.cachedSessionId);
                return tokenResponse;
            } catch (Exception ex) {
                this.cachedToken = null;
                this.cachedSessionId = null;
                logError("Failed to create token for storage ID: " + storageId, ex);
                throw ex;
            }
        }, "createTokenRequest");
    }

    // Added method for testability
    protected String getDecryptedPassword(StorageModel storage) {
        return credentialEncryptor.decrypt(storage.getEncryptedPassword());
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
                        ApiUtil.getURI(ApiConstants.SESSIONS_ENDPOINT + "/" + this.cachedSessionId),
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
                logError("Failed to validate token for session ID: " + this.cachedSessionId, ex);
                throw ex;
            }
        }, "validateToken");
    }

    @Override
    public String getToken() {
        return this.cachedToken;
    }

    public String getMetricCollectorId(StorageModel storage) {
        String storageId = storage.getStorageId();
        String organizationId = storage.getOrganizationId();
        if (storageId == null || organizationId == null) {
            logWarn("Storage ID or Organization ID not available, using default Metric Collector ID");
            return "unknown_unknown";
        }
        return storageId + "_" + organizationId;
    }

    public void validateCredentials(String userId, String password) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        if (!USER_ID_PATTERN.matcher(userId).matches()) {
            logError("Invalid User ID format: contains disallowed characters", null);
            throw new IllegalArgumentException("User ID contains invalid characters. Allowed: alphanumeric, !#$%&'*+-./=?@^_`{|}~");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches() || password.contains(" ")) {
            logError("Invalid password format: contains disallowed characters or spaces", null);
            throw new IllegalArgumentException("Password contains invalid characters or spaces. Allowed: ASCII characters except space");
        }
    }

    public String createBasicAuthHeader(String userId, String password) {
        String credentials = userId + ":" + password;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encodedCredentials;
    }

    public String getRequestBody(TokenRequest tokenRequest) throws JsonProcessingException {
        String requestBody = "{}";
        if (tokenRequest != null) {
            requestBody = OBJECT_MAPPER.writeValueAsString(tokenRequest);
        }
        return requestBody;
    }

    public void logInfo(String message) {
        Optional<StorageModel> storageOpt = storageService.getStorage(storageId);
        String metricCollectorId = storageOpt.isPresent() ? getMetricCollectorId(storageOpt.get()) : "unknown_unknown";
        ConfigurationUtil.getLogger().info(formatLog("INFO", metricCollectorId, message));
    }

    public void logWarn(String message) {
        Optional<StorageModel> storageOpt = storageService.getStorage(storageId);
        String metricCollectorId = storageOpt.isPresent() ? getMetricCollectorId(storageOpt.get()) : "unknown_unknown";
        ConfigurationUtil.getLogger().warn(formatLog("WARN", metricCollectorId, message));
    }

    public void logError(String message, Throwable ex) {
        Optional<StorageModel> storageOpt = storageService.getStorage(storageId);
        String metricCollectorId = storageOpt.isPresent() ? getMetricCollectorId(storageOpt.get()) : "unknown_unknown";
        String logMessage = formatLog("ERROR", metricCollectorId, message + (ex != null ? ": " + ex.getMessage() : ""));
        ConfigurationUtil.getLogger().error(logMessage, ex);
    }

    public String formatLog(String level, String metricCollectorId, String message) {
        return String.format("%s [%s] %s Authentication: %s",
                ZonedDateTime.now().format(LOG_DATE_FORMAT),
                level,
                metricCollectorId,
                message);
    }
}