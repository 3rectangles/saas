/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.apikey;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKeyDAO, String> {
	Optional<ApiKeyDAO> findByKeyAndDisabledOnIsNull(String apiKey);

	Optional<ApiKeyDAO> findByKeyNameAndPartnerIdAndDisabledOnIsNull(final String keyName, final String partnerId);

	List<ApiKeyDAO> findAllByKeyNameAndDisabledOnIsNull(final String keyName);
}
