/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.config;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ZoomNewConfigFactory {
	private final AWSSecretsManager awsSecretsManager;
	private final ObjectMapper objectMapper;

	@Bean(name = "zoomNewConfig")
	public ZoomNewConfig config() throws JsonProcessingException {
		final String zoomApiKeySecretString = this.awsSecretsManager.getSecretValue(
				new GetSecretValueRequest().withSecretId("zoom_new_sdk_key")).getSecretString();

		final Map<String, String> zoomApiKeySecret = this.objectMapper.readValue(zoomApiKeySecretString,
				new TypeReference<>() {
				});

		return new ZoomNewConfig(
				zoomApiKeySecret.keySet().iterator().next(),
				zoomApiKeySecret.values().iterator().next());
	}
}
