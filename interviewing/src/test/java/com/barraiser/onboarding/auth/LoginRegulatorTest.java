/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth;

import com.barraiser.onboarding.auth.dal.LoginBlacklistDAO;
import com.barraiser.onboarding.auth.dal.LoginBlacklistRepository;
import com.barraiser.onboarding.auth.dal.UserLoginActivityDAO;
import com.barraiser.onboarding.auth.loginRegulator.LoginRegulator;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LoginRegulatorTest {

	@InjectMocks
	private LoginRegulator loginRegulator;

	@Mock
	private DynamicAppConfigProperties dynamicAppConfigProperties;

	@Mock
	private LoginBlacklistRepository loginBlacklistRepository;

	@Mock
	private LoginActivityManager loginActivityManager;

	private static final String DYNAMO_FAILED_LOGIN_COUNT_WINDOW_KEY = "failed-login-count-window";

	@Test
	public void shouldGiveUserLoginIsToBeBlocked() {

		when(this.loginBlacklistRepository.findByEmailIdAndTtlGreaterThan(any(), any()))
				.thenReturn(Optional.of(
						LoginBlacklistDAO.builder().build()));

		assertEquals(Boolean.TRUE, this.loginRegulator.isUserLoginBlocked("test_email_id"));
	}

	@Test
	public void shouldGiveUserLoginIsNOTToBeBlocked() {
		when(this.loginBlacklistRepository.findByEmailIdAndTtlGreaterThan(any(), any()))
				.thenReturn(Optional.empty());

		assertEquals(Boolean.FALSE, this.loginRegulator.isUserLoginBlocked("test_email_id"));

	}

	// ------- Testing failed login attempts

	@Test
	public void testGetFailedLoginAttemptsScenario1() {

		/**
		 * Case : No failed login attempts
		 */

		when(this.dynamicAppConfigProperties
				.getInt(DYNAMO_FAILED_LOGIN_COUNT_WINDOW_KEY))
						.thenReturn(1800);

		when(this.loginActivityManager.getAllFailedLoginAttemptsForEmailAfterTime(any(), any()))
				.thenReturn(List.of());

		assertEquals(0, this.loginRegulator.getFailedLoginAttempts("test_email_id").size());
	}

	@Test
	public void testGetFailedLoginAttemptsScenario2() {

		/**
		 * Case : failed attempts counting window = 30 mins
		 * - 2 failed login attempts
		 * - user never blocked in the last 30 mins
		 * - user made no successful login in the last 30 mins
		 *
		 * Expected : 2 failed login attempts
		 */

		when(this.dynamicAppConfigProperties
				.getInt(DYNAMO_FAILED_LOGIN_COUNT_WINDOW_KEY))
						.thenReturn(1800);

		when(this.loginActivityManager.getAllFailedLoginAttemptsForEmailAfterTime(any(), any()))
				.thenReturn(List.of(
						UserLoginActivityDAO.builder().emailId(
								"test_email").createdOn(Instant.now())
								.build(),
						UserLoginActivityDAO.builder().emailId(
								"test_email").createdOn(Instant.now())
								.build()));

		when(this.loginActivityManager.getLatestLoginAttemptForEmail("test_email", Boolean.TRUE))
				.thenReturn(Optional.empty());

		when(this.loginBlacklistRepository.findTopByEmailIdOrderByTtlDesc("test_email"))
				.thenReturn(Optional.empty());

		assertEquals(2, this.loginRegulator.getFailedLoginAttempts("test_email_id").size());
	}

	@Test
	public void testGetFailedLoginAttemptsScenario3() {

		/**
		 * Case : failed attempts counting window = 30 mins
		 * - user blocked for 15 mins in the last 30 mins
		 * - 2 failed login attempts after the blocking
		 * - 2 failed login attempts before the blocking
		 * - user made no successful login in the last 30 mins
		 *
		 * Expected => Only 2 failed login attempts (The ones after being blocked)
		 */

		final Instant currentTime = Instant.now();

		when(this.dynamicAppConfigProperties
				.getInt(DYNAMO_FAILED_LOGIN_COUNT_WINDOW_KEY))
						.thenReturn(1800);

		when(this.loginActivityManager.getAllFailedLoginAttemptsForEmailAfterTime(any(), any()))
				.thenReturn(List.of(
						// post blacklist period completion
						UserLoginActivityDAO.builder().emailId(
								"test_email_id").createdOn(currentTime.minusSeconds(2 * 60))
								.build(),
						UserLoginActivityDAO.builder().emailId(
								"test_email_id").createdOn(currentTime.minusSeconds(3 * 60))
								.build(),

						// before blacklist period
						UserLoginActivityDAO.builder().emailId(
								"test_email_id").createdOn(currentTime.minusSeconds(25 * 60))
								.build(),
						UserLoginActivityDAO.builder().emailId(
								"test_email_id").createdOn(currentTime.minusSeconds(26 * 60))
								.build()

				));

		when(this.loginActivityManager.getLatestLoginAttemptForEmail(any(), any()))
				.thenReturn(Optional.empty());

		when(this.loginBlacklistRepository.findTopByEmailIdOrderByTtlDesc(any()))
				.thenReturn(Optional.of(
						LoginBlacklistDAO.builder()
								.emailId(any())
								.ttl(currentTime.minusSeconds(5 * 60).getEpochSecond())
								.build()));

		assertEquals(2, this.loginRegulator.getFailedLoginAttempts("test_email_id").size());
	}

	@Test
	public void testGetFailedLoginAttemptsScenario4() {

		/**
		 * Case : failed attempts counting window = 30 mins
		 * - user blocked for 15 mins in the last 30 mins
		 * - 2 failed login attempts after the blocking
		 * - 2 failed login attempts before the blocking
		 * - user made 1 successful login in the last 30 mins ie
		 * after the first failed attempt after being blocked
		 *
		 * Expected => Only 1 failed login attempts (The last one)
		 */

		final Instant currentTime = Instant.now();

		when(this.dynamicAppConfigProperties
				.getInt(DYNAMO_FAILED_LOGIN_COUNT_WINDOW_KEY))
						.thenReturn(1800);

		when(this.loginActivityManager.getAllFailedLoginAttemptsForEmailAfterTime(any(), any()))
				.thenReturn(List.of(
						// post blacklist period completion
						UserLoginActivityDAO.builder().emailId(
								"test_email_id").createdOn(currentTime.minusSeconds(2 * 60))
								.build(),
						UserLoginActivityDAO.builder().emailId(
								"test_email_id").createdOn(currentTime.minusSeconds(4 * 60))
								.build(),

						// before blacklist period
						UserLoginActivityDAO.builder().emailId(
								"test_email_id").createdOn(currentTime.minusSeconds(25 * 60))
								.build(),
						UserLoginActivityDAO.builder().emailId(
								"test_email_id").createdOn(currentTime.minusSeconds(26 * 60))
								.build()

				));

		when(this.loginActivityManager.getLatestLoginAttemptForEmail(any(), any()))
				.thenReturn(Optional.of(
						UserLoginActivityDAO.builder()
								.createdOn(currentTime.minusSeconds(3 * 60))
								.build()));

		when(this.loginBlacklistRepository.findTopByEmailIdOrderByTtlDesc(any()))
				.thenReturn(Optional.of(
						LoginBlacklistDAO.builder()
								.emailId(any())
								.ttl(currentTime.minusSeconds(5 * 60).getEpochSecond())
								.build()));

		assertEquals(1, this.loginRegulator.getFailedLoginAttempts("test_email_id").size());
	}

	// -------- Testing failed login attempts window start

	@Test
	public void testGetFailedLoginAttemptsWindowStartTimeScenario1() {

		/**
		 * Case :
		 * no successful login by user
		 * user was never blacklisted
		 */
		final Long time = Instant.now().minusSeconds(1800).getEpochSecond();

		when(this.loginActivityManager.getLatestLoginAttemptForEmail("test_email", Boolean.TRUE))
				.thenReturn(Optional.empty());

		when(this.loginBlacklistRepository.findTopByEmailIdOrderByTtlDesc("test_email"))
				.thenReturn(Optional.empty());

		assertEquals(time, this.loginRegulator.getFailedLoginAttemptsWindowStartTime("test_email", time));

	}

	@Test
	public void testGetFailedLoginAttemptsWindowStartTimeScenario2() {

		/**
		 * Case :
		 * failed attempts counting window = 30 mins
		 * successful login inside of the 30 mins
		 * user was never blacklisted
		 *
		 *
		 * Expected result => time of the successful login
		 */

		final Long time = Instant.now().minusSeconds(1800).getEpochSecond();
		final Instant timeOfLatestSuccessfulLogin = Instant.now().minusSeconds(900);

		when(this.loginActivityManager.getLatestLoginAttemptForEmail("test_email", Boolean.TRUE))
				.thenReturn(
						Optional.of(UserLoginActivityDAO.builder()
								.emailId("test_email")
								.createdOn(timeOfLatestSuccessfulLogin)
								.build()));

		when(this.loginBlacklistRepository.findTopByEmailIdOrderByTtlDesc("test_email"))
				.thenReturn(Optional.empty());

		assertEquals(timeOfLatestSuccessfulLogin.getEpochSecond(),
				this.loginRegulator.getFailedLoginAttemptsWindowStartTime("test_email", time));
	}

	@Test
	public void testGetFailedLoginAttemptsWindowStartTimeScenario3() {

		/**
		 * Case :
		 * failed attempts counting window = 30 mins
		 * successful login by user inside of the 30 mins configured
		 * user blacklist ttl complete after the successful login
		 *
		 * Expected => Time of user account blacklist completion
		 */

		final Long time = Instant.now().minusSeconds(1800).getEpochSecond();
		final Instant timeOfLatestSuccessfulLogin = Instant.now().minusSeconds(900);
		final Long userBlacklistTTL = Instant.now().minusSeconds(450).getEpochSecond();

		when(this.loginActivityManager.getLatestLoginAttemptForEmail("test_email", Boolean.TRUE))
				.thenReturn(
						Optional.of(UserLoginActivityDAO.builder()
								.emailId("test_email")
								.createdOn(timeOfLatestSuccessfulLogin)
								.build()));

		when(this.loginBlacklistRepository.findTopByEmailIdOrderByTtlDesc("test_email"))
				.thenReturn(Optional.of(
						LoginBlacklistDAO.builder()
								.emailId("test_email")
								.ttl(userBlacklistTTL)
								.build()));

		assertEquals(userBlacklistTTL, this.loginRegulator.getFailedLoginAttemptsWindowStartTime("test_email", time));
	}

}
