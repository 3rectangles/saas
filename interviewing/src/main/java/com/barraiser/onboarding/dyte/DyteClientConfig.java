/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dyte;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import feign.RequestInterceptor;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;

@Log4j2
@AllArgsConstructor
public class DyteClientConfig {

	private AWSSecretsManager awsSecretsManager;

	@SneakyThrows
	public String getAPIKey() {
		final String dyteAPIKey = this.awsSecretsManager.getSecretValue(
				new GetSecretValueRequest().withSecretId("DyteAPIKey")).getSecretString();

		return dyteAPIKey;
	}

	@Bean
	public RequestInterceptor requestInterceptor() {
		return requestTemplate -> {
			requestTemplate.header("Content-Type", "application/json");
			requestTemplate.header("Authorization", this.getAPIKey());
		};
	}
}
