/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.communication;

import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.communication.channels.email.EmailService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.barraiser.onboarding.common.Constants.GENERIC_EMAIL_TEMPLATE;

@Log4j2
@Component
@AllArgsConstructor
public class ErrorCommunication {
	private final EmailService emailService;
	private final StaticAppConfigValues staticAppConfigValues;

	public void informErrorToOps(final String subject, final String errorMessage) throws Exception {
		final Map<String, String> data = new HashMap<>();
		data.put("body", errorMessage);
		final String opsEmailId = this.staticAppConfigValues.getInterviewLifecycleInformationEmail();
		this.emailService.sendEmail(opsEmailId, subject, GENERIC_EMAIL_TEMPLATE, data, null);
	}

	public void sendFailureEmailToTech(final String subject, final Exception exception) throws Exception {
		final Map<String, String> emailData = new HashMap<>();
		final String emailBody = "The details of the error are follows : " + ExceptionUtils.getStackTrace(exception);
		emailData.put("body", emailBody);
		final String fromEmail = this.staticAppConfigValues.getInterviewLifecycleInformationEmail();

		final List<String> toEmail = new ArrayList<>();
		toEmail.add(this.staticAppConfigValues.getInterviewNotificationEmail());

		this.emailService.sendEmail(fromEmail, subject, GENERIC_EMAIL_TEMPLATE, toEmail, emailData, null);
	}

	public void sendEvaluationAdditionFailureEmailToPOC(final String subject, final String body, final String pocEmail)
			throws Exception {
		final Map<String, String> emailData = new HashMap<>();
		emailData.put("body", body);
		final String fromEmail = this.staticAppConfigValues.getInterviewLifecycleInformationEmail();

		final List<String> toEmail = new ArrayList<>();
		toEmail.add(pocEmail);

		this.emailService.sendEmail(
				fromEmail,
				subject,
				GENERIC_EMAIL_TEMPLATE,
				toEmail,
				emailData,
				null);
	}
}
