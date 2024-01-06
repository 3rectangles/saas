/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.otp;

import com.barraiser.common.graphql.types.OtpVerificationResult;
import com.barraiser.onboarding.auth.AuthenticationManager;
import com.barraiser.onboarding.auth.loginRegulator.LoginRegulator;
import com.barraiser.onboarding.graphql.Constants;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.google.i18n.phonenumbers.NumberParseException;
import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
public class VerifyOtp implements NamedDataFetcher<OtpVerificationResult> {
	private final AuthenticationManager authenticationManager;
	private final LoginRegulator loginRegulator;

	@Override
	public OtpVerificationResult get(final DataFetchingEnvironment environment) throws NumberParseException {
		final String email = environment.getArgument("email");
		final String submittedOtp = environment.getArgument("otp");
		log.info("Verifying otp for email:{} with otp: {}", email, submittedOtp);
		this.validateInput(email, submittedOtp);
		boolean isAuthenticated = false;

		if (this.loginRegulator.isUserLoginBlocked(email)) {
			log.info("Email id : " + email + " is currently blocked from logging in.");
			return OtpVerificationResult.builder()
					.isLoginBlocked(Boolean.TRUE)
					.maximumAllowedLoginAttempts(this.loginRegulator.getMaximumAllowedLoginAttempts())
					.accountLockoutPeriodInMinutes(this.loginRegulator.getAccountLockoutPeriodInMinutes())
					.build();
		}

		final boolean isOtpVerified = this.authenticationManager.verifySubmittedOtp(email, submittedOtp);

		if (isOtpVerified) {
			log.info("OTP verified, generating cookies");
			try {
				final List<ResponseCookie> cookies = this.authenticationManager.authenticateWithOtp(email,
						submittedOtp);
				final GraphQLContext context = environment.getContext();
				context.put(Constants.CONTEXT_KEY_COOKIES, cookies);
				log.info("Cookies generated");
				isAuthenticated = true;
			} catch (final Exception ex) {
				log.info("Otp is correct, user is not registered." + ex.getMessage());
			}
		}

		final Boolean isLoginAttemptSuccessful = (isAuthenticated && isOtpVerified);
		this.loginRegulator.registerLoginActivity(email, submittedOtp, isLoginAttemptSuccessful);

		return OtpVerificationResult.builder()
				.authenticated(isAuthenticated)
				.verified(isOtpVerified)
				.remainingLoginAttempts(
						!isLoginAttemptSuccessful ? this.loginRegulator.getRemainingLoginAttempts(email) : 0)
				.isLoginBlocked(this.loginRegulator.isUserLoginBlocked(email))
				.maximumAllowedLoginAttempts(this.loginRegulator.getMaximumAllowedLoginAttempts())
				.accountLockoutPeriodInMinutes(this.loginRegulator.getAccountLockoutPeriodInMinutes())
				.build();
	}

	public void validateInput(final String email, final String submittedOtp) {
		if (email == null) {
			throw new IllegalArgumentException("No phone or email provided");
		}

		if (submittedOtp == null) {
			throw new IllegalArgumentException("No OTP provided for email " + email);
		}
	}

	@Override
	public String name() {
		return "verifyOtp";
	}

	@Override
	public String type() {
		return QUERY_TYPE;
	}
}
