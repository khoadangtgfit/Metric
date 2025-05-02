package com.hitachids.metriccollector.auth.service;

import com.hitachids.metriccollector.auth.model.TokenResponse;

public interface AuthenticationService {
	TokenResponse createTokenRequest(String userId, String password) throws Exception;
	boolean validateToken() throws Exception;
	String getToken();
}