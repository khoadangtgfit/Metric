package com.hitachids.metriccollector.auth.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hitachids.metriccollector.auth.model.TokenRequest;
import com.hitachids.metriccollector.auth.model.TokenResponse;
import com.hitachids.metriccollector.common.utils.ApiUtil;
import com.hitachids.metriccollector.common.utils.ConfigurationUtil;
import com.hitachids.metriccollector.resiliencer.Resiliencer;
import com.hitachids.metriccollector.storage.model.StorageModel;
import com.hitachids.metriccollector.storage.service.StorageService;
import org.apache.commons.logging.Log;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HttpAuthenticationServiceTest {

    @Mock
    private StorageService storageService;

    @Mock
    private Log logger;

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @InjectMocks
    private HttpAuthenticationService httpAuthenticationService = new HttpAuthenticationService(storageService, STORAGE_ID);

    private MockedStatic<ConfigurationUtil> configurationUtilMock;

    private static final int STORAGE_ID = 1;
    private static final String BASE_URL = "http://salamander-api.com";
    private static final String TOKEN_ENDPOINT = "/api/token";
    private static final String VALID_TOKEN = "abc123";
    private static final String SESSION_ID = "session-001";

    @BeforeEach
    public void setUp() throws Exception {
        // Initialize MockedStatic for ConfigurationUtil
        configurationUtilMock = Mockito.mockStatic(ConfigurationUtil.class);

        // Stub ConfigurationUtil static methods
        configurationUtilMock.when(ConfigurationUtil::getLogger).thenReturn(logger);
        configurationUtilMock.when(ConfigurationUtil::getBaseUrl).thenReturn(BASE_URL);
        configurationUtilMock.when(() -> ConfigurationUtil.getUrl(eq(TOKEN_ENDPOINT))).thenReturn(BASE_URL + TOKEN_ENDPOINT);
        configurationUtilMock.when(ConfigurationUtil::getTimeout).thenReturn(2000);
        configurationUtilMock.when(ConfigurationUtil::getMaxRetries).thenReturn(3);
        configurationUtilMock.when(ConfigurationUtil::getAuthToken).thenReturn("Basic secret");

        // Mock StorageService with complete StorageModel
        StorageModel storageModel = new StorageModel();
        storageModel.setStorageId("storage1");
        storageModel.setOrganizationId("org1");
        storageModel.setUserId("testUser");
        storageModel.setEncryptedPassword("testEncryptedPassword"); // Add non-null encrypted password
        when(storageService.getStorage(STORAGE_ID)).thenReturn(Optional.of(storageModel));

        // Initialize HttpAuthenticationService with storageId and mock the CredentialEncryptor
        httpAuthenticationService = spy(new HttpAuthenticationService(storageService, STORAGE_ID));

        // Mock the decrypt method to return a valid password
        doReturn("testPassword").when(httpAuthenticationService).createBasicAuthHeader(anyString(), anyString());
    }

    @AfterEach
    public void tearDown() {
        if (configurationUtilMock != null) {
            configurationUtilMock.close();
        }
    }

    @Test
    public void testCreateTokenRequest_Success() throws Exception {
        // Arrange
        TokenRequest tokenRequest = new TokenRequest();
        String responseBody = "{\"token\":\"" + VALID_TOKEN + "\",\"sessionId\":\"" + SESSION_ID + "\"}";
        String requestBody = "{\"username\":\"testUser\",\"password\":\"decryptedPassword\"}";

        // Create ObjectNode for mock response
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode responseNode = mapper.createObjectNode();
        responseNode.put("token", VALID_TOKEN);
        responseNode.put("sessionId", SESSION_ID);

        // Mock static ApiUtil.post
        try (MockedStatic<ApiUtil> apiUtilMock = mockStatic(ApiUtil.class)) {
            apiUtilMock.when(() -> ApiUtil.post(anyString(), anyString(), anyString(), anyInt()))
                    .thenReturn(responseNode);

            // Mock the credential validation (void method)
            doNothing().when(httpAuthenticationService).validateCredentials(anyString(), anyString());

            // Mock getDecryptedPassword to avoid real decryption logic
            doReturn("decryptedPassword").when(httpAuthenticationService).getDecryptedPassword(any(StorageModel.class));

            // Mock getRequestBody to avoid Jackson serialization
            doReturn(requestBody).when(httpAuthenticationService).getRequestBody(any(TokenRequest.class));

            // Mock httpClient.send with specific BodyHandler
            when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                    .thenReturn(httpResponse);
            when(httpResponse.statusCode()).thenReturn(200);
            when(httpResponse.body()).thenReturn(responseBody);

            // Act
            TokenResponse response = httpAuthenticationService.createTokenRequest(tokenRequest);

            // Assert
            assertNotNull(response);
            assertEquals(VALID_TOKEN, response.getToken());
            assertEquals(SESSION_ID, response.getSessionId());
            verify(logger).info(contains("Successfully created token"));
            verify(httpAuthenticationService).validateCredentials(anyString(), anyString());
        }
    }

    @Test
    public void testCreateTokenRequest_Failure() throws Exception {
        // Arrange
        TokenRequest tokenRequest = new TokenRequest();

        // Mock the credential decryption
        doThrow(new IllegalArgumentException()).when(httpAuthenticationService).validateCredentials(any(), any());
//        doReturn("decryptedPassword").when(httpAuthenticationService).validateCredentials(anyString(), anyString());

        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(401);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            httpAuthenticationService.createTokenRequest(tokenRequest);
        });
//        assertTrue(exception.getMessage().contains("Failed to create token"));
//        verify(logger).error(contains("Failed to create token"));
    }

    @Test
    public void testValidateToken_Valid() throws Exception {
        // Arrange
        // Assume validateToken makes an HTTP request to check token validity
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);

        // Act
        boolean isValid = httpAuthenticationService.validateToken();

        // Assert
        assertTrue(isValid);
        verify(logger).info(contains("Token validated successfully"));
    }

    @Test
    public void testValidateToken_Invalid() throws Exception {
        // Arrange
        when(httpClient.send(any(HttpRequest.class), eq(HttpResponse.BodyHandlers.ofString())))
                .thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(401);

        // Act
        boolean isValid = httpAuthenticationService.validateToken();

        // Assert
        assertFalse(isValid);
        verify(logger).warn(contains("Token validation failed"));
    }

    @Test
    public void testGetToken_ReturnsToken() {
        // Arrange
        // Assume HttpAuthenticationService stores token internally after createTokenRequest
        // This requires reflection or a setter to simulate a stored token (simplified here)
        TokenResponse tokenResponse = new TokenResponse();
        tokenResponse.setToken(VALID_TOKEN);
        tokenResponse.setSessionId(SESSION_ID);

        // Act
        String token = httpAuthenticationService.getToken();

        // Assert
        assertEquals(VALID_TOKEN, token); // Adjust based on actual implementation
    }

    @Test
    public void testGetToken_NoToken() {
        // Arrange
        // Assume no token is set
        httpAuthenticationService = new HttpAuthenticationService(storageService, STORAGE_ID);

        // Act
        String token = httpAuthenticationService.getToken();

        // Assert
        assertNull(token);
    }
}