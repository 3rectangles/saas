/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.communication.channels.email;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.communication.EmailHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class EmailQueueProcessor {
	private final AmazonSQS amazonSQS;
	private final ObjectMapper objectMapper;
	private final StaticAppConfigValues staticAppConfigValues;
	private final List<EmailHandler> emailHandler;

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void processEmail() throws IOException {
		final ReceiveMessageResult response = this.amazonSQS.receiveMessage(
				new ReceiveMessageRequest()
						.withQueueUrl(this.staticAppConfigValues.getEmailEventSQSUrl())
						.withWaitTimeSeconds(10)
						.withMaxNumberOfMessages(1));

		for (final Message message : response.getMessages()) {
			final EmailEvent emailEvent = this.objectMapper.readValue(message.getBody(), EmailEvent.class);
			try {
				if (emailEvent.getObjective() != null) {
					this.emailHandler.stream()
							.filter(x -> x.objective().equals(emailEvent.getObjective()))
							.findFirst()
							.orElse(null)
							.process(emailEvent);
				}
				this.amazonSQS.deleteMessage(
						new DeleteMessageRequest(
								this.staticAppConfigValues.getEmailEventSQSUrl(),
								message.getReceiptHandle()));
			} catch (final Exception e) {
				log.error(
						"Error in processing email event: {} because of {} ", message.getBody(), e);
				throw new RuntimeException(
						"Error in processing email event" + message.getBody(), e);
			}
		}
	}
}
