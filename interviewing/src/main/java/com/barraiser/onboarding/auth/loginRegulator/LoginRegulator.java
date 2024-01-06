/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.loginRegulator;

import com.barraiser.onboarding.auth.LoginActivityManager;
import com.barraiser.onboarding.auth.dal.LoginBlacklistDAO;
import com.barraiser.onboarding.auth.dal.LoginBlacklistRepository;
import com.barraiser.onboarding.auth.dal.UserLoginActivityDAO;
import com.barraiser.onboarding.communication.channels.email.EmailService;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
@Component
public class LoginRegulator {

	private final LoginBlacklistRepository loginBlacklistRepository;
	private final DynamicAppConfigProperties dynamicAppConfigProperties;
	private final LoginActivityManager loginActivityManager;
	private final EmailService emailService;

	private static final String DYNAMO_ACCOUNT_LOCKOUT_PERIOD_KEY = "account-lockout-period";
	private static final String DYNAMO_MAX_ALLOWED_FAILED_LOGINS_KEY = "max-allowed-failed-login-attempts";
	private static final String DYNAMO_FAILED_LOGIN_COUNT_WINDOW_KEY = "failed-login-count-window";

	private static final String TECH_EMAIL_ADDRESS = "tech@barraiser.com";
	private static final String USER_ACCOUNT_LOCKED_EMAIL_TEMPLATE = "user_account_lockout_email";

	public static final Integer MINUTES_IN_AN_HOUR = 60;

	public Boolean isUserLoginBlocked(final String emailId) {
		Optional<LoginBlacklistDAO> loginBlacklistDAOOptional = this.loginBlacklistRepository
				.findByEmailIdAndTtlGreaterThan(emailId, Instant.now().getEpochSecond());

		return (loginBlacklistDAOOptional.isPresent()) ? Boolean.TRUE : Boolean.FALSE;
	}

	public Integer getMaximumAllowedLoginAttempts() {
		return this.dynamicAppConfigProperties
				.getInt(DYNAMO_MAX_ALLOWED_FAILED_LOGINS_KEY);
	}

	public Integer getAccountLockoutPeriodInMinutes() {
		return this.dynamicAppConfigProperties
				.getInt(DYNAMO_ACCOUNT_LOCKOUT_PERIOD_KEY) / MINUTES_IN_AN_HOUR;
	}

	public void registerLoginActivity(final String emailId, final String otp, final Boolean isLoginAttemptSuccessful) {

		this.loginActivityManager.saveLoginActivity(emailId, otp, isLoginAttemptSuccessful);

		if (Boolean.FALSE.equals(isLoginAttemptSuccessful)) {
			final Boolean isUserToBeBlocked = this.isUserLoginToBeBlocked(emailId);

			if (isUserToBeBlocked) {
				log.info("Login to be blocked for user : " + emailId);
				this.blockUser(emailId);
				this.notifyTeamOnUserBlocking(emailId);
			}
		}
	}

	private void blockUser(final String emailId) {
		final Integer lockoutPeriod = this.dynamicAppConfigProperties.getInt(DYNAMO_ACCOUNT_LOCKOUT_PERIOD_KEY);

		this.loginBlacklistRepository.save(
				LoginBlacklistDAO.builder()
						.id(UUID.randomUUID().toString())
						.emailId(emailId)
						.ttl(Instant.now().getEpochSecond() + lockoutPeriod)
						.build());
	}

	private void notifyTeamOnUserBlocking(final String emailId) {
		final String subject = String.format("Alert! %s locked out", emailId);

		final Map<String, String> dataMap = new HashMap<>();
		dataMap.put("user_email_id", emailId);

		try {
			this.emailService.sendEmail(TECH_EMAIL_ADDRESS, subject, USER_ACCOUNT_LOCKED_EMAIL_TEMPLATE, dataMap, null);
		} catch (Exception e) {
			log.error("Failed to notify barraiser team on user account login locking for email : " + emailId);
		}
	}

	private Boolean isUserLoginToBeBlocked(final String emailId) {

		final Integer maxAllowedFailedLoginAttempts = this.dynamicAppConfigProperties
				.getInt(DYNAMO_MAX_ALLOWED_FAILED_LOGINS_KEY);

		final List<UserLoginActivityDAO> filteredFailedLoginAttemptsInTimeWindow = this.getFailedLoginAttempts(emailId);

		return (filteredFailedLoginAttemptsInTimeWindow.size() >= maxAllowedFailedLoginAttempts) ? Boolean.TRUE
				: Boolean.FALSE;
	}

	public Integer getRemainingLoginAttempts(final String emailId) {

		if (this.isUserLoginBlocked(emailId)) {
			return 0;
		}

		final Integer maxAllowedFailedLoginAttempts = this.dynamicAppConfigProperties
				.getInt(DYNAMO_MAX_ALLOWED_FAILED_LOGINS_KEY);

		final List<UserLoginActivityDAO> filteredFailedLoginAttemptsInTimeWindow = this.getFailedLoginAttempts(emailId);

		return filteredFailedLoginAttemptsInTimeWindow.size() <= maxAllowedFailedLoginAttempts
				? maxAllowedFailedLoginAttempts - filteredFailedLoginAttemptsInTimeWindow.size()
				: 0;
	}

	public List<UserLoginActivityDAO> getFailedLoginAttempts(final String emailId) {

		final Integer failedLoginAttemptsWindow = this.dynamicAppConfigProperties
				.getInt(DYNAMO_FAILED_LOGIN_COUNT_WINDOW_KEY);

		final Long failedLoginAttemptWindowFixedStartTime = Instant.now().getEpochSecond() - failedLoginAttemptsWindow;
		final List<UserLoginActivityDAO> failedLoginAttemptsInTimeWindow = this.loginActivityManager
				.getAllFailedLoginAttemptsForEmailAfterTime(emailId, failedLoginAttemptWindowFixedStartTime);

		final Long failedLoginAttemptsWindowStartTime = this.getFailedLoginAttemptsWindowStartTime(emailId,
				failedLoginAttemptWindowFixedStartTime);

		final List<UserLoginActivityDAO> filteredFailedLoginAttemptsInTimeWindow = failedLoginAttemptsInTimeWindow
				.stream().filter(x -> x.getCreatedOn().getEpochSecond() > failedLoginAttemptsWindowStartTime)
				.collect(Collectors.toList());

		return filteredFailedLoginAttemptsInTimeWindow;
	}

	/**
	 * This function basically considers three variables
	 * The last successful login attempt , the end time of the last user blacklist
	 * and
	 * start time of the window (lets say now - 30 mins) during which we want to
	 * evaluate if the user is to be blacklisted from login.
	 * <p>
	 * It returns the time post which we will count all the failed login attempts
	 * to see if the user is to be blacklisted or not
	 * <p>
	 * NOTE : This function is used to ensure that once the user was blacklisted
	 * and that period is over , or makes a successful attempt post being
	 * blacklisted ,
	 * then the time window for consideration starts from after that , or it will
	 * end up being a sliding window wherein the user gets unblacklisted much later
	 * than lets say
	 * the 20 mins for which they were configured to be blacklisted.
	 *
	 * @param emailId
	 * @param failedLoginAttemptsWindowMinimumStartTime
	 * @return
	 */
	public Long getFailedLoginAttemptsWindowStartTime(final String emailId,
			final Long failedLoginAttemptsWindowMinimumStartTime) {
		final Optional<UserLoginActivityDAO> latestSuccessfulLoginAttempt = this.loginActivityManager
				.getLatestLoginAttemptForEmail(emailId, Boolean.TRUE);

		final Long latestSuccessfulLoginAttemptTime = latestSuccessfulLoginAttempt.isPresent()
				? latestSuccessfulLoginAttempt.get().getCreatedOn().getEpochSecond()
				: 0l;

		Optional<LoginBlacklistDAO> latestBlacklistCompletionEntry = this.loginBlacklistRepository
				.findTopByEmailIdOrderByTtlDesc(emailId);

		final Long latestBlacklistTimeForUser = latestBlacklistCompletionEntry.isPresent()
				? latestBlacklistCompletionEntry.get().getTtl()
				: 0l;

		return Math.max(failedLoginAttemptsWindowMinimumStartTime,
				Math.max(latestSuccessfulLoginAttemptTime, latestBlacklistTimeForUser));
	}

}
