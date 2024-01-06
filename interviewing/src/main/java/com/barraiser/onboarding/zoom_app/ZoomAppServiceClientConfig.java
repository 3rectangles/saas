/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.zoom_app;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import feign.RequestInterceptor;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

@Log4j2
@AllArgsConstructor
public class ZoomAppServiceClientConfig {
	private final AWSSecretsManager awsSecretsManager;

	@Value("${zoom.app.api-key}")
	private String apiKeySecretName;

	public String getAuthToken() {
		return this.awsSecretsManager
				.getSecretValue(new GetSecretValueRequest().withSecretId(apiKeySecretName))
				.getSecretString();
	}

	@Bean
	public RequestInterceptor requestInterceptor() {
		return requestTemplate -> {
			requestTemplate.header("Authorization", this.getAuthToken());
		};
	}
}
