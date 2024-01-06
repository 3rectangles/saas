/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.sso.microsoft;

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
public class MicrosoftOAuthConfig implements SSOConfig {
	private final AWSSecretsManager awsSecretsManager;
	private final ObjectMapper objectMapper;
	private final StaticAppConfigValues staticAppConfigValues;
	private Map<String, String> secret;

	@PostConstruct
	private void init() throws JsonProcessingException {
		final String secretString = this.awsSecretsManager.getSecretValue(
				new GetSecretValueRequest().withSecretId("MicrosoftOAuthSecret")).getSecretString();
		this.secret = this.objectMapper.readValue(secretString, new TypeReference<>() {
		});
	}

	@Override
	public String source() {
		return SSOProvider.MICROSOFT;
	}

	@Override
	public String getClientId() {
		return this.secret.get("client_id");
	}

	@Override
	public String getClientSecret() {
		return this.secret.get("client_secret");
	}

	@Override
	public String getTokenEndpoint() {
		return "https://login.microsoftonline.com/common/oauth2/v2.0/token";
	}

	@Override
	public String getAuthorizationEndpoint() {
		return "https://login.microsoftonline.com/common/oauth2/v2.0/token";
	}
}
