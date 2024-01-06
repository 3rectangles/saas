/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.barraiser.onboarding.auth.otp.OtpManager;
import com.barraiser.onboarding.auth.otp.VerifyOtp;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.OtpDAO;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationManagerTest {
	@Mock
	private AWSCognitoIdentityProvider awsCognitoIdentityProvider;
	@Mock
	private JwtConsumer jwtConsumer;
	@Mock
	private StaticAppConfigValues staticAppConfigValues;
	@Mock
	private OtpManager otpManager;
	@InjectMocks
	private AuthenticationManager authenticationManager;

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	@Test
	public void shouldNotVerifyUserIfLatestOtpIsEmpty() {
		final String email = "email@barraisertest.com";
		final String otp = "otp";
		when(this.otpManager.getLatestUnExpiredOTP(email))
				.thenReturn(Optional.empty());
		final Boolean isOtpVerified = this.authenticationManager.verifySubmittedOtp(email, otp);
		assertFalse(isOtpVerified);
	}

	@Test
	public void shouldNotVerifyIfOtpIsNotLatest() {
		final String email = "email@barraisertest.com";
		final String otp = "otp";
		when(this.otpManager.getLatestUnExpiredOTP(email))
				.thenReturn(Optional.ofNullable(OtpDAO.builder().otp("latestOtp").build()));
		final Boolean isOtpVerified = this.authenticationManager.verifySubmittedOtp(email, otp);
		assertFalse(isOtpVerified);
	}

	@Test
	public void shouldVerifyIfOtpIsEqualToLatestOtp() {
		final String email = "email@barraisertest.com";
		final String otp = "1008";
		when(this.otpManager.getLatestUnExpiredOTP(email))
				.thenReturn(Optional.ofNullable(OtpDAO.builder().otp("1008").build()));
		final Boolean isOtpVerified = this.authenticationManager.verifySubmittedOtp(email, otp);
		assertTrue(isOtpVerified);
	}

}
