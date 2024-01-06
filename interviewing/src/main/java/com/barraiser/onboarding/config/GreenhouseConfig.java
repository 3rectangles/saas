/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.config;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import org.springframework.context.annotation.Configuration;

import java.util.Base64;
import java.util.Map;

@Configuration
@Log4j2
@AllArgsConstructor
public class GreenhouseConfig {
	private static final String GREENHOUSE_SECRET_NAME = "greenhouse_API_key_secret";
	private final AWSSecretsManager awsSecretsManager;
	private final ObjectMapper objectMapper;

	private Map<String, String> getGreenhouseSecret() throws Exception {
		final String greenhouseApiKeySecret = this.awsSecretsManager
				.getSecretValue(
						new GetSecretValueRequest().withSecretId(GREENHOUSE_SECRET_NAME))
				.getSecretString();

		return this.objectMapper.readValue(greenhouseApiKeySecret, new TypeReference<>() {
		});
	}

	@SneakyThrows
	public String getAuthorizationHeaderForAPICall() {
		final Map<String, String> greenhouseAPIKeySecret = this.getGreenhouseSecret();

		final String basicAuthenticationKey = String.format(
				"%s:%s",
				greenhouseAPIKeySecret.get("username"),
				greenhouseAPIKeySecret.get("password"));

		return String.format(
				"Basic %s", Base64.getEncoder().encodeToString(basicAuthenticationKey.getBytes()));
	}
}
