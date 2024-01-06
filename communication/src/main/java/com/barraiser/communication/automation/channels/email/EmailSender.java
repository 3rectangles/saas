/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.email;

import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.barraiser.communication.automation.pipeline.CommunicationProcessor;
import com.barraiser.communication.automation.constants.Channel;
import com.barraiser.communication.automation.channels.email.dto.EmailMessage;
import com.barraiser.communication.automation.channels.email.dto.EmailData;
import com.barraiser.communication.automation.channels.email.dto.EmailRecipient;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Log4j2
@Component
@AllArgsConstructor
public class EmailSender implements CommunicationProcessor<EmailData> {
	private final AmazonSimpleEmailService amazonSimpleEmailService;
	private final CommZapierEmailService commZapierEmailService;

	@Override
	public Channel getChannel() {
		return Channel.EMAIL;
	}

	@Override
	public void process(EmailData data) throws IOException {
		final EmailRecipient recipient = data.getRecipient();
		final EmailMessage message = data.getMessage();

		if (!this.hasRecepients(recipient)) {
			log.info("NO_RECEPIENT:Not sending email as there is no recepient available");
			return;
		}

		final SendEmailRequest request = new SendEmailRequest()
				.withDestination(this.getDestination(recipient))
				.withMessage(this.getMessage(message))
				.withSource(this.getEmailHeader(data.getFromEmail(), message.getHeader()));

		request.setReplyToAddresses(Collections.singleton("support@barraiser.com"));
		this.amazonSimpleEmailService.sendEmail(request);
	}

	private Destination getDestination(final EmailRecipient recipient) {
		Destination destination = new Destination()
				.withToAddresses(recipient.getToEmails());

		if (recipient.getCcEmails() != null && !recipient.getCcEmails().isEmpty()) {
			destination = destination.withCcAddresses(recipient.getCcEmails());
		}
		if (recipient.getBccEmails() != null && !recipient.getBccEmails().isEmpty()) {
			destination = destination.withBccAddresses(recipient.getBccEmails());
		}

		return destination;
	}

	private Message getMessage(final EmailMessage body) {
		return new Message()
				.withBody(new Body()
						.withHtml(new Content()
								.withCharset("UTF-8")
								.withData(body.getBody())))
				.withSubject(new Content()
						.withCharset("UTF-8")
						.withData(body.getSubject()));
	}

	private String getEmailHeader(final String fromEmail, String emailHeader) {
		emailHeader = emailHeader == null ? "BarRaiser" : emailHeader;
		return (fromEmail != null) ? emailHeader + "<" + fromEmail + ">"
				: emailHeader + "<" + "no-reply@barraiser.com" + ">";
	}

	private Boolean hasRecepients(final EmailRecipient emailRecipient) {
		return (emailRecipient.getToEmails() != null && emailRecipient.getToEmails().size() > 0)
				|| (emailRecipient.getCcEmails() != null && emailRecipient.getCcEmails().size() > 0)
				|| (emailRecipient.getBccEmails() != null && emailRecipient.getBccEmails().size() > 0);
	}
}
