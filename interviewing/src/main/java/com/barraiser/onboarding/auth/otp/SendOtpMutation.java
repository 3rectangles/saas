/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.otp;

import com.barraiser.onboarding.auth.recaptcha.RecaptchaService;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.communication.common.CommunicationStaticAppConfig;
import com.barraiser.onboarding.dal.OtpDAO;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

@Component
@Log4j2
@RequiredArgsConstructor
public class SendOtpMutation implements NamedDataFetcher<Boolean> {
	public static final String SEND_OTP_FLAG = "send-otp";
	private final OtpManager otpManager;
	private final PhoneNumberUtil phoneNumberUtil;
	private final RecaptchaService recaptchaService;
	private final CommunicationStaticAppConfig communicationStaticAppConfig;

	@Override
	public Boolean get(final DataFetchingEnvironment environment)
			throws NumberParseException, IOException, URISyntaxException {
		final String email = environment.getArgument("email");
		final String recaptchaToken = environment.getArgument(("recaptchaToken"));
		final String redirectUrl = this.communicationStaticAppConfig.getLoginLink();

		if (email == null) {
			throw new IllegalArgumentException("No email provided");
		}

		/*
		 * sendOtp() API would return False if Recaptcha validation is failed
		 */
		log.info("Email input in sendOtp() API call: {}", email);
		if (!this.recaptchaService.isRecaptchaValid(recaptchaToken)) {
			return Boolean.FALSE;
		}

		Optional<OtpDAO> otp = this.otpManager.getLatestUnExpiredOTP(email);

		if (otp.isEmpty() || otp.get().getIsVerified() || !otp.get().getIsValid()) {
			otp = Optional.of(this.otpManager.generateAndSaveOtp(email));
		}

		this.otpManager.sendEmailwithMagicLink(email, otp.get().getOtp(), redirectUrl);

		return Boolean.TRUE;
	}

	private String getFormattedPhone(final DataFetchingEnvironment environment) throws NumberParseException {
		final String phone = environment.getArgument("phone");
		if (phone == null) {
			return null;
		}
		final Phonenumber.PhoneNumber inputPhone = this.phoneNumberUtil.parse(phone, "IN");
		return this.phoneNumberUtil.format(inputPhone, PhoneNumberUtil.PhoneNumberFormat.E164);
	}

	@Override
	public String name() {
		return "sendOtp";
	}

	@Override
	public String type() {
		return MUTATION_TYPE;
	}
}
