/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.otp;

import com.barraiser.onboarding.auth.magicLink.MagicLinkManager;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.communication.channels.email.EmailService;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.OtpDAO;
import com.barraiser.onboarding.dal.OtpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Component
@RequiredArgsConstructor
@Service
public class OtpManager {
	public static final String OTP_TTL = "otp-ttl";
	private final DynamicAppConfigProperties appConfigProperties;
	private final OtpRepository otpRepository;
	private final EmailService emailService;
	private final MagicLinkManager magicLinkManager;
	private final Long MAGIC_LINK_EXPIRATION = 15 * 60L;

	private final StaticAppConfigValues staticAppConfigValues;

	public Optional<OtpDAO> getLatestUnExpiredOTP(final String email) {
		final Optional<OtpDAO> lastOtp = this.otpRepository.findFirstByEmailOrderByCreatedOnDesc(email);

		if (lastOtp.isPresent() && lastOtp.get().getIsValid() && !lastOtp.get().getIsVerified()) {
			// Check for age of the OTP
			if (lastOtp.get().getTtl() >= Instant.now().getEpochSecond()) {
				return lastOtp;
			}
		}
		return Optional.empty();
	}

	public void markOtpVerified(final OtpDAO otp) {
		this.otpRepository.save(otp.toBuilder()
				.isVerified(true)
				.isValid(false)
				.build());
	}

	public OtpDAO generateAndSaveOtp(final String email) {
		this.markAllPreviousOtpInvalid(email);
		final OtpDAO generatedOtp = OtpDAO.builder()
				.email(email)
				.otp(this.generateRandomOtp())
				.isValid(true)
				.isVerified(false)
				.ttl(Instant.now().plus(this.appConfigProperties.getInt(OTP_TTL), ChronoUnit.SECONDS).getEpochSecond())
				.build();

		return this.otpRepository.save(generatedOtp);
	}

	private void markAllPreviousOtpInvalid(final String email) {
		final List<OtpDAO> previousInvalidOtps = this.otpRepository.findAllByEmailAndIsValidTrue(email).stream()
				.map(x -> x.toBuilder().isValid(false).build())
				.collect(Collectors.toList());
		this.otpRepository.saveAll(previousInvalidOtps);
	}

	public void sendEmail(final String email, final String otp) throws IOException {
		final Map<String, String> data = new HashMap<>();
		data.put("otp_message", String.format(this.staticAppConfigValues.getLoginOtpMessage(), otp));
		this.emailService.sendEmail(email, "BarRaiser Login OTP", "send_email_otp", data, null);
	}

	public void sendEmailwithMagicLink(final String email, final String otp, final String redirectUrl)
			throws IOException {
		final Map<String, String> data = new HashMap<>();
		data.put("otp_message", String.format(this.staticAppConfigValues.getLoginOtpMessage(), otp));
		try {
			final String magicLinkUrl = this.magicLinkManager.generateMagicUrl(redirectUrl, email,
					MAGIC_LINK_EXPIRATION);
			data.put("magic_link", magicLinkUrl);
		} catch (final URISyntaxException e) {
			throw new RuntimeException(e);
		}
		this.emailService.sendEmail(email, "BarRaiser Login OTP", "send_email_otp", data, null);
	}

	private String generateRandomOtp() {
		final Random rand = new Random();
		final int otp = rand.nextInt(10000);
		final String prefix;
		if (otp < 10) {
			prefix = "000";
		} else if (otp < 100) {
			prefix = "00";
		} else if (otp < 1000) {
			prefix = "0";
		} else {
			prefix = "";
		}
		log.info(String.format("Sending Otp : %s", prefix + otp));
		return prefix + otp;
	}

}
