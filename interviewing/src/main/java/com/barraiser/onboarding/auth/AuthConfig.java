/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth;

import com.barraiser.onboarding.common.StaticAppConfigValues;
import lombok.AllArgsConstructor;
import org.jose4j.jwk.HttpsJwks;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.resolvers.HttpsJwksVerificationKeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@AllArgsConstructor
public class AuthConfig {
	private final StaticAppConfigValues staticAppConfigValues;

	@Bean(name = "jwtConsumerWithoutExpirationTime")
	public JwtConsumer getJwtConsumerWithoutExpirationTimeRequired() {
		final HttpsJwks httpsJkws = new HttpsJwks(this.staticAppConfigValues.getJwksDownloadUrl());
		final HttpsJwksVerificationKeyResolver httpsJwksKeyResolver = new HttpsJwksVerificationKeyResolver(httpsJkws);
		return new JwtConsumerBuilder()
				.setVerificationKeyResolver(httpsJwksKeyResolver)
				.setSkipAllValidators()
				.setExpectedIssuer(this.staticAppConfigValues.getExpectedIssuerCognito())
				.setExpectedAudience(this.staticAppConfigValues.getCognitoBackendClientId())
				.build();
	}
}
