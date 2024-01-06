/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserLoginActivityRepository extends JpaRepository<UserLoginActivityDAO, String> {

	List<UserLoginActivityDAO> findByEmailIdAndIsLoginAttemptSuccessfulAndCreatedOnGreaterThanEqual(
			final String email, final Boolean isLoginAttemptSuccessful, final Instant loginAttemptTime);

	Optional<UserLoginActivityDAO> findTopByEmailIdAndIsLoginAttemptSuccessfulOrderByCreatedOnDesc(
			final String email, final Boolean isLoginAttemptSuccessful);
}
