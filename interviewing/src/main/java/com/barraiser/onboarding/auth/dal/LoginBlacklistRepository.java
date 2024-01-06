/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.dal;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginBlacklistRepository extends JpaRepository<LoginBlacklistDAO, String> {

	Optional<LoginBlacklistDAO> findByEmailIdAndTtlGreaterThan(final String emailId, final Long ttl);

	Optional<LoginBlacklistDAO> findTopByEmailIdOrderByTtlDesc(final String emailId);
}
