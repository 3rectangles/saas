/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.config;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.barraiser.ats_integrations.common.ATSIntegrationsStaticAppConfigValues;
import com.barraiser.ats_integrations.lever.LeverSecret;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Log4j2
@AllArgsConstructor
public class LeverSecretFactory {
	private final AWSSecretsManager awsSecretsManager;
	private final ObjectMapper objectMapper;

	private final ATSIntegrationsStaticAppConfigValues appConfigValues;

	@Bean
	public LeverSecret getLeverSecret() throws Exception {
		final String leverAPIKeySecret = this.awsSecretsManager
				.getSecretValue(
						new GetSecretValueRequest()
								.withSecretId(this.appConfigValues.getLeverSecretNames()))
				.getSecretString();

		return this.objectMapper
				.readValue(
						leverAPIKeySecret,
						LeverSecret.class);
	}
}
