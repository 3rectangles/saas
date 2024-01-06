/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.otp;

import com.barraiser.onboarding.auth.AuthenticationManager;
import com.barraiser.onboarding.user.PartnerEmployeeWhiteLister;
import graphql.schema.DataFetchingEnvironment;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class VerifyOtpOrSignUpForBGSTest {
	@Mock
	private AuthenticationManager authenticationManager;
	@Mock
	private VerifyOtp verifyOtp;
	@Mock
	private PartnerEmployeeWhiteLister partnerEmployeeWhiteLister;
	@InjectMocks
	private VerifyOtpOrSignUpForBGS verifyOtpOrSignUpForBGS;
	@Mock
	private DataFetchingEnvironment environment;

	@Rule
	public ExpectedException exceptionRule = ExpectedException.none();

	@Test
	public void shouldThrowExceptionWhenEmailAndPhoneBothNull() {
		this.exceptionRule.expect(IllegalArgumentException.class);
		this.exceptionRule.expectMessage("No phone or email provided");
		when(this.environment.getArgument("email"))
				.thenReturn(null);
		doThrow(new IllegalArgumentException("No phone or email provided"))
				.when(this.verifyOtp).validateInput(null, null);
		this.verifyOtpOrSignUpForBGS.get(this.environment);
	}

	@Test
	public void shouldThrowExceptionForNullOTP() {
		this.exceptionRule.expect(IllegalArgumentException.class);
		final String email = "email@barraisertest.com";
		this.exceptionRule.expectMessage("No OTP provided for email " + email);
		when(this.environment.getArgument("email"))
				.thenReturn(email);
		when(this.environment.getArgument("otp"))
				.thenReturn(null);
		doThrow(new IllegalArgumentException("No OTP provided for email " + email))
				.when(this.verifyOtp).validateInput(email, null);
		this.verifyOtpOrSignUpForBGS.get(this.environment);
	}

	@Test
	public void shouldNotVerifyUserOtpIsNotVerified() {
		final String email = "email@barraisertest.com";
		final String otp = "otp";
		final String partnerId = "test_p_c";
		when(this.environment.getArgument("email"))
				.thenReturn(email);
		when(this.environment.getArgument("otp"))
				.thenReturn(otp);
		when(this.authenticationManager.verifySubmittedOtp(email, otp))
				.thenReturn(false);
		this.verifyOtpOrSignUpForBGS.get(this.environment);
		verify(this.partnerEmployeeWhiteLister, never())
				.signUpUserIfWhiteListed(eq(email), eq(partnerId));
		verify(this.authenticationManager, never()).authenticateWithOtp(eq(email), eq(otp));
	}

	@Test
	public void shouldVerifyUserIfOtpIsVerified() {
		final String email = "email@barraisertest.com";
		final String otp = "1008";
		final String partnerId = "test_p_c";
		when(this.environment.getArgument("email"))
				.thenReturn(email);
		when(this.environment.getArgument("otp"))
				.thenReturn(otp);
		when(this.authenticationManager.verifySubmittedOtp(email, otp))
				.thenReturn(true);
		this.verifyOtpOrSignUpForBGS.get(this.environment);
		verify(this.partnerEmployeeWhiteLister).signUpUserIfWhiteListed(eq(email), eq(partnerId));
		verify(this.authenticationManager).authenticateWithOtp(eq(email), eq(otp));
	}
}
