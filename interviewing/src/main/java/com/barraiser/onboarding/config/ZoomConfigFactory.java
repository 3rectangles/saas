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
public class ZoomConfigFactory {
	private final AWSSecretsManager awsSecretsManager;
	private final ObjectMapper objectMapper;

	@Bean(name = "zoomConfig")
	public ZoomConfig config() throws JsonProcessingException {
		final String zoomClientIdSecretString = this.awsSecretsManager.getSecretValue(
				new GetSecretValueRequest().withSecretId("zoom_server_to_server_oauth_secret")).getSecretString();

		final String zoomAccountIdString = this.awsSecretsManager.getSecretValue(
				new GetSecretValueRequest().withSecretId("zoom_server_to_server_oauth_account_id")).getSecretString();

		final Map<String, String> zoomClientIdSecret = this.objectMapper.readValue(zoomClientIdSecretString,
				new TypeReference<>() {
				});

		final Map<String, String> zoomAccountId = this.objectMapper.readValue(zoomAccountIdString,
				new TypeReference<>() {
				});

		return new ZoomConfig(
				zoomClientIdSecret.keySet().iterator().next(),
				zoomClientIdSecret.values().iterator().next(),
				zoomAccountId.keySet().iterator().next());
	}

}
