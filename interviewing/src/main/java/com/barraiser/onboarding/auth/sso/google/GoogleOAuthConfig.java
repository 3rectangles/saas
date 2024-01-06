/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.sso.google;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.barraiser.onboarding.auth.sso.SSOConfig;
import com.barraiser.onboarding.auth.sso.SSOProvider;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
@Log4j2
@RequiredArgsConstructor
public class GoogleOAuthConfig implements SSOConfig {
	private final AWSSecretsManager awsSecretsManager;
	private final ObjectMapper objectMapper;
	private final GoogleSSOClient ssoClient;
	private final StaticAppConfigValues staticAppConfigValues;

	private Map<String, String> secret;

	@PostConstruct
	private void init() throws JsonProcessingException {
		final String secretString = this.awsSecretsManager.getSecretValue(
				new GetSecretValueRequest().withSecretId("GoogleOAuthSecret")).getSecretString();
		this.secret = this.objectMapper.readValue(secretString, new TypeReference<>() {
		});
	}

	@Override
	public String source() {
		return SSOProvider.GOOGLE;
	}

	public String getClientId() {
		return secret.get("client_id");
	}

	public String getClientSecret() {
		return secret.get("client_secret");
	}

	@Override
	public String getTokenEndpoint() {
		return this.ssoClient.getOpenIdConfig().getTokenEndpoint();
	}

	@Override
	public String getAuthorizationEndpoint() {
		return this.ssoClient.getOpenIdConfig().getAuthorizationEndpoint();
	}
}
