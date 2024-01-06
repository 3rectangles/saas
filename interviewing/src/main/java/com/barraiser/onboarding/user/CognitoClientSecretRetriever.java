/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class CognitoClientSecretRetriever {
	private final StaticAppConfigValues staticAppConfigValues;
	private final AWSSecretsManager awsSecretsManager;

	@SneakyThrows
	public String retrieve() {
		final GetSecretValueResult result = this.awsSecretsManager
				.getSecretValue(new GetSecretValueRequest()
						.withSecretId(this.staticAppConfigValues.getCognitoBackendClientSecretId()));

		return result.getSecretString();
	}
}
