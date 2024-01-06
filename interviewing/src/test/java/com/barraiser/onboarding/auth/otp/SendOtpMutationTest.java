/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.otp;

import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.OtpDAO;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import graphql.schema.DataFetchingEnvironment;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static com.barraiser.onboarding.auth.otp.SendOtpMutation.SEND_OTP_FLAG;
import static org.mockito.Mockito.*;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class SendOtpMutationTest {
	@Mock
	private OtpManager otpManager;
	@Spy
	private final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

	@Mock
	private DynamicAppConfigProperties appConfigProperties;
	@Mock
	private DataFetchingEnvironment environment;

	@InjectMocks
	private SendOtpMutation sendOtpMutation;

	@Test
	public void shouldSendOtpWhenNoOtpPresent() throws NumberParseException, IOException, URISyntaxException {
		// GIVEN
		final String email = "an email";
		final String generatedOtp = "7867";
		when(this.environment.getArgument("email")).thenReturn(email);
		// TODO: test email as well
		when(this.otpManager.generateAndSaveOtp(null)).thenReturn(OtpDAO.builder()
				.otp(generatedOtp)
				.isVerified(false)
				.isValid(true)
				.createdOn(Instant.now())
				.build());
		when(this.appConfigProperties.getBoolean(SEND_OTP_FLAG)).thenReturn(Boolean.TRUE);

		// WHEN
		this.sendOtpMutation.get(this.environment);

		// THEN

		verify(this.otpManager).sendEmail(email, generatedOtp);
	}

	@Test
	public void shouldNotSendOtpIfConfigIsDisabled() throws NumberParseException, IOException, URISyntaxException {
		// GIVEN
		final String email = "an email";
		final String generatedOtp = "7867";
		when(this.environment.getArgument("email")).thenReturn(email);
		when(this.otpManager.generateAndSaveOtp(null)).thenReturn(OtpDAO.builder()
				.otp(generatedOtp)
				.isVerified(false)
				.isValid(true)
				.createdOn(Instant.now())
				.build());
		when(this.appConfigProperties.getBoolean(SEND_OTP_FLAG)).thenReturn(Boolean.TRUE);

		// WHEN
		this.sendOtpMutation.get(this.environment);

		// THEN

		verify(this.otpManager, timeout(0)).sendEmail(email, generatedOtp);
	}

	@Test
	public void shouldSendOtpWhenNotExpiredOtpPresent() throws NumberParseException, IOException, URISyntaxException {
		// GIVEN
		final String email = "an email";
		final String generatedOtp = "7867";
		final String previousOtp = "7821";
		when(this.environment.getArgument("email")).thenReturn(email);
		when(this.otpManager.generateAndSaveOtp(null)).thenReturn(OtpDAO.builder()
				.otp(generatedOtp)
				.isVerified(false)
				.isValid(true)
				.createdOn(Instant.now())
				.build());
		when(this.otpManager.getLatestUnExpiredOTP(null)).thenReturn(Optional.of(OtpDAO.builder()
				.createdOn(Instant.now().minus(120, ChronoUnit.SECONDS))
				.otp(previousOtp)
				.isValid(true)
				.isVerified(false)
				.build()));
		when(this.appConfigProperties.getBoolean(SEND_OTP_FLAG)).thenReturn(Boolean.TRUE);

		// WHEN
		this.sendOtpMutation.get(this.environment);

		// THEN

		verify(this.otpManager).sendEmail(email, previousOtp);
	}

	@Test
	public void shouldSendNewOtpIfAlreadyVerified() throws NumberParseException, IOException, URISyntaxException {
		// GIVEN
		final String email = "an email";
		final String generatedOtp = "7867";
		final String previousOtp = "7821";
		when(this.environment.getArgument("email")).thenReturn(email);
		when(this.otpManager.generateAndSaveOtp(null)).thenReturn(OtpDAO.builder()
				.otp(generatedOtp)
				.isVerified(true)
				.isValid(true)
				.createdOn(Instant.now())
				.build());
		when(this.otpManager.getLatestUnExpiredOTP(null)).thenReturn(Optional.of(OtpDAO.builder()
				.createdOn(Instant.now().minus(301 /* 5 min + 1 sec */, ChronoUnit.SECONDS))
				.otp(previousOtp)
				.isValid(true)
				.isVerified(false)
				.build()));
		when(this.appConfigProperties.getBoolean(SEND_OTP_FLAG)).thenReturn(Boolean.TRUE);

		// WHEN
		this.sendOtpMutation.get(this.environment);

		// THEN
		verify(this.otpManager).sendEmail(email, generatedOtp);
	}

	@Test
	public void shouldSendNewOtpIfNotValid() throws NumberParseException, IOException, URISyntaxException {
		// GIVEN
		final String email = "+917836074940";
		final String generatedOtp = "7867";
		final String previousOtp = "7821";
		when(this.environment.getArgument("email")).thenReturn(email);
		when(this.otpManager.generateAndSaveOtp(null)).thenReturn(OtpDAO.builder()
				.otp(generatedOtp)
				.isVerified(true)
				.isValid(true)
				.createdOn(Instant.now())
				.build());
		when(this.otpManager.getLatestUnExpiredOTP(null)).thenReturn(Optional.of(OtpDAO.builder()
				.createdOn(Instant.now().minus(301 /* 5 min + 1 sec */, ChronoUnit.SECONDS))
				.otp(previousOtp)
				.isValid(false)
				.isVerified(false)
				.build()));
		when(this.appConfigProperties.getBoolean(SEND_OTP_FLAG)).thenReturn(Boolean.TRUE);

		// WHEN
		this.sendOtpMutation.get(this.environment);

		// THEN

		verify(this.otpManager).sendEmail(email, generatedOtp);
	}

}
