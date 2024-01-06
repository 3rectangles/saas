/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.sso;

import com.barraiser.onboarding.user.UserInformationManagementHelper;
import com.google.api.client.googleapis.auth.oauth2.GoogleRefreshTokenRequest;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
public class SSOAccountVerifier {
	private final List<SSOConfig> configs;
	private final UserInformationManagementHelper userInformationManagementHelper;

	public void verify(final String source, final String refreshToken) throws IOException, GeneralSecurityException {
		final SSOConfig config = this.configs.stream().filter(c -> c.source().equals(source)).findFirst().get();
		// This will use the refresh token to get new tokens. This will fail if the user
		// has revoked access to our app.
		// Note : Although we are using GoogleRefreshTokenRequest, by providing a token
		// server url we can hit any oauth provider
		new GoogleRefreshTokenRequest(new NetHttpTransport(),
				new JacksonFactory(),
				refreshToken,
				config.getClientId(),
				config.getClientSecret())
						.setGrantType("refresh_token")
						.setTokenServerUrl(new GenericUrl(config.getTokenEndpoint()))
						.execute();
	}

	public void clearSSOAccountDetails(final String userId) {
		this.userInformationManagementHelper.updateUserAttributes(userId, Map.of(
				"custom:sso_source", "",
				"custom:sso_refresh_token", ""));
	}
}
