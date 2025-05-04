package com.hitachids.metriccollector.auth.service;

import com.hitachids.metriccollector.auth.model.TokenRequest;
import com.hitachids.metriccollector.auth.model.TokenResponse;

/**
 * Interface for authentication services handling Salamander API authentication.
 */
public interface AuthenticationService {

    /**
     * Creates a new authentication token request to the Salamander API.
     *
     * @param tokenRequest The token request parameters, or null for default settings
     * @return TokenResponse containing the session token and session ID
     * @throws Exception If authentication fails or an error occurs
     */
    TokenResponse createTokenRequest(TokenRequest tokenRequest) throws Exception;

    /**
     * Validates the current authentication token.
     *
     * @return true if the token is valid, false otherwise
     * @throws Exception If validation fails or an error occurs
     */
    boolean validateToken() throws Exception;

    /**
     * Retrieves the current authentication token.
     *
     * @return The current token, or null if no valid token exists
     */
    String getToken();
}