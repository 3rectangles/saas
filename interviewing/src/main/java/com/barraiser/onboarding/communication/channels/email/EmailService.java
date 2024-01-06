/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.communication.channels.email;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.barraiser.onboarding.common.MustacheFormattingUtil;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
@AllArgsConstructor
public class EmailService {
	private final AmazonSimpleEmailService amazonSimpleEmailService;
	private final StaticAppConfigValues staticAppConfigValues;
	private final MustacheFormattingUtil mustacheFormattingUtil;

	private final InterviewingZapierEmailService interviewingZapierEmailService;

	public void sendEmailWithAllOptions(final String fromEmail, final String subject, final String template,
			final List<String> toEmail,
			final List<String> ccEmails, final List<String> bccEmails, final Map<String, String> data,
			final String emailHeader) throws IOException {

		final String body = this.mustacheFormattingUtil.formatDataToText(template, data);

		final SendEmailRequest request = new SendEmailRequest()
				.withDestination(new Destination()
						.withToAddresses(toEmail)
						.withCcAddresses(ccEmails)
						.withBccAddresses(bccEmails))
				.withMessage(new Message()
						.withBody(new Body()
								.withHtml(new Content().withCharset("UTF-8")
										.withData(this.mustacheFormattingUtil.formatDataToText(template, data))))
						.withSubject(new Content()
								.withCharset("UTF-8").withData(subject)))
				.withSource(this.getEmailHeader(fromEmail, emailHeader));

		this.amazonSimpleEmailService.sendEmail(request);

	}

	public void sendEmail(final String toEmail, final String subject, final String template,
			final Map<String, String> data, final String emailHeader) throws IOException {
		final String body = this.mustacheFormattingUtil.formatDataToText(template, data);
		final SendEmailRequest request = new SendEmailRequest()
				.withDestination(new Destination()
						.withToAddresses(toEmail))
				.withMessage(new Message()
						.withBody(new Body()
								.withHtml(new Content().withCharset("UTF-8")
										.withData(this.mustacheFormattingUtil.formatDataToText(template, data))))
						.withSubject(new Content()
								.withCharset("UTF-8").withData(subject)))
				.withSource(this.getEmailHeader(this.staticAppConfigValues.getInterviewLifecycleInformationEmail(),
						emailHeader));

		this.amazonSimpleEmailService.sendEmail(request);

	}

	public void sendEmailForObjectData(final String fromEmail, final String subject, final String template,
			final List<String> toEmail,
			final List<String> ccEmails, final Map<String, Object> data, final String emailHeader) throws IOException {
		data.put("subject", subject);
		try {

			final String body = this.mustacheFormattingUtil.formatObjectDataToText(template, data);
			final SendEmailRequest request = new SendEmailRequest()
					.withDestination(new Destination()
							.withToAddresses(toEmail)
							.withCcAddresses(ccEmails))
					.withMessage(new Message()
							.withBody(new Body()
									.withHtml(new Content().withCharset("UTF-8").withData(
											this.mustacheFormattingUtil.formatObjectDataToText(template, data))))
							.withSubject(new Content()
									.withCharset("UTF-8").withData(subject)))
					.withSource(this.getEmailHeader(fromEmail, emailHeader));
			this.amazonSimpleEmailService.sendEmail(request);
		} catch (Exception e) {
			log.info(e);
		}
	}

	/**
	 * With CC without Bcc
	 *
	 * @param toEmail
	 * @param subject
	 * @param template
	 * @param data
	 * @throws IOException
	 */
	public void sendEmail(String fromEmail, final String subject, final String template, final List<String> toEmail,
			final List<String> ccEmails, final Map<String, String> data, final String emailHeader) throws IOException {
		final List<String> bccEmails = null;
		this.sendEmailWithAllOptions(fromEmail, subject, template, toEmail, ccEmails, bccEmails, data, emailHeader);
	}

	/**
	 * Without CC and Bcc
	 *
	 * @param toEmail
	 * @param subject
	 * @param template
	 * @param data
	 * @throws IOException
	 */
	public void sendEmail(final String fromEmail, final String subject, final String template,
			final List<String> toEmail, final Map<String, String> data, final String emailHeader) throws Exception {
		final List<String> bccEmails = null;
		final List<String> ccEmails = null;
		this.sendEmailWithAllOptions(fromEmail, subject, template, toEmail, ccEmails, bccEmails, data, emailHeader);
	}

	private String getEmailHeader(final String fromEmail, String emailHeader) {
		emailHeader = emailHeader == null ? "BarRaiser" : emailHeader;
		return emailHeader + "<" + fromEmail + ">";
	}
}
