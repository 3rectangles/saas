/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.config;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.barraiser.ats_integrations.common.ATSIntegrationsStaticAppConfigValues;
import com.barraiser.ats_integrations.merge.MergeSecret;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Log4j2
@AllArgsConstructor
public class MergeSecretFactory {
	private final AWSSecretsManager awsSecretsManager;
	private final ObjectMapper objectMapper;
	private final ATSIntegrationsStaticAppConfigValues atsIntegrationsStaticAppConfigValues;

	@Bean
	public MergeSecret getMergeSecret() throws Exception {
		final String mergeDevSecret = this.awsSecretsManager
				.getSecretValue(new GetSecretValueRequest()
						.withSecretId(this.atsIntegrationsStaticAppConfigValues
								.getMergeDevSecretNames()))
				.getSecretString();

		return this.objectMapper
				.readValue(
						mergeDevSecret,
						MergeSecret.class);
	}
}
