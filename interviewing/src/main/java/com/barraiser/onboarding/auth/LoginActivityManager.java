/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth;

import com.barraiser.onboarding.auth.dal.UserLoginActivityDAO;
import com.barraiser.onboarding.auth.dal.UserLoginActivityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@AllArgsConstructor
@Component
public class LoginActivityManager {

	private final UserLoginActivityRepository userLoginActivityRepository;

	private static final String LOGIN_KEY_TYPE_OTP = "OTP";

	public void saveLoginActivity(final String emailId, final String otp, final Boolean isLoginAttemptSuccessful) {
		this.userLoginActivityRepository.save(
				UserLoginActivityDAO.builder()
						.id(UUID.randomUUID().toString())
						.emailId(emailId)
						.loginKeyType(LOGIN_KEY_TYPE_OTP)
						.loginKey(otp)
						.isLoginAttemptSuccessful(isLoginAttemptSuccessful)
						.build());
	}

	public Optional<UserLoginActivityDAO> getLatestLoginAttemptForEmail(final String email,
			final Boolean isAttemptSuccessful) {
		return this.userLoginActivityRepository
				.findTopByEmailIdAndIsLoginAttemptSuccessfulOrderByCreatedOnDesc(email, isAttemptSuccessful);
	}

	public List<UserLoginActivityDAO> getAllFailedLoginAttemptsForEmailAfterTime(final String email,
			final Long time) {
		return this.getAllLoginAttemptsForEmailGreaterThanTime(email, Boolean.FALSE, time);
	}

	private List<UserLoginActivityDAO> getAllLoginAttemptsForEmailGreaterThanTime(final String email,
			final Boolean isAttemptSuccessful, final Long time) {
		return this.userLoginActivityRepository
				.findByEmailIdAndIsLoginAttemptSuccessfulAndCreatedOnGreaterThanEqual(email, isAttemptSuccessful,
						Instant.ofEpochSecond(time));
	}

}
