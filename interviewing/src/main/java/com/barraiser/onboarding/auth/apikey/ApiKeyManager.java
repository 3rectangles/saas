/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.apikey;

import com.barraiser.commons.auth.UserRole;
import lombok.AllArgsConstructor;
import org.jose4j.lang.JoseException;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class ApiKeyManager {
	private final ApiKeyRepository apiKeyRepository;

	public String issueApiKey(final String userId, final String partnerId, final String keyName,
			final List<UserRole> roles) throws JoseException {
		final String apiKey = this.generateKey();
		this.apiKeyRepository.save(ApiKeyDAO.builder()
				.id(UUID.randomUUID().toString())
				.keyName(keyName)
				.key(apiKey)
				.partnerId(partnerId)
				.userId(userId)
				.roles(roles.stream().map(UserRole::getRole).collect(Collectors.toList()))
				.build());
		return apiKey;
	}

	public void disableApiKey(final String apiKey) {
		final ApiKeyDAO apiKeyDAO = this.apiKeyRepository
				.findByKeyAndDisabledOnIsNull(apiKey)
				.orElseThrow(() -> new RuntimeException("No such api key present"));

		this.apiKeyRepository
				.save(apiKeyDAO.toBuilder()
						.disabledOn(Instant.now())
						.build());
	}

	public Optional<ApiKeyDAO> getApiKey(final String key) {
		return this.apiKeyRepository.findByKeyAndDisabledOnIsNull(key);
	}

	private String generateKey() {
		return Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes());
	}
}
