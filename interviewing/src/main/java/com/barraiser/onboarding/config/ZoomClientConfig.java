/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.config;

import com.barraiser.onboarding.zoom.ZoomAuthClient;
import feign.RequestInterceptor;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import java.util.Base64;

@Log4j2
@AllArgsConstructor
public class ZoomClientConfig {
	@Autowired
	@Qualifier("zoomConfig")
	private final ZoomConfig config;

	private final ZoomAuthClient zoomAuthClient;

	private static final String ACCOUNT_CREDENTIALS_GRANT_TYPE = "account_credentials";

	public String getEncodedAuthorizationToken() {

		String clientIdSecret = this.config.getClientId() + ":" + this.config.getClientSecret();
		byte[] originalBytes = clientIdSecret.getBytes();
		byte[] encodedBytes = Base64.getEncoder().encode(originalBytes);

		return new String(encodedBytes);
	}

	public String getAccessToken() {

		return this.zoomAuthClient.getToken(
				"Basic " + this.getEncodedAuthorizationToken(),
				ACCOUNT_CREDENTIALS_GRANT_TYPE,
				this.config.getAccountId()).getAccessToken();
	}

	@Bean("ZoomServerRequestInterceptor")
	public RequestInterceptor requestInterceptor() {
		return requestTemplate -> {
			requestTemplate.header("Content-Type", "application/json");
			requestTemplate.header("Authorization", "Bearer " + this.getAccessToken());
		};
	}
}
