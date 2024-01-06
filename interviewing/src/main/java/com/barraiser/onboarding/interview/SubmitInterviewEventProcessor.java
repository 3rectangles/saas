/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.barraiser.commons.eventing.sqs.SQSMessageHandler;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.interview.evaluation.BgsDataGenerator;
import com.barraiser.onboarding.interview.pojo.InterviewFeedbackSubmittedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class SubmitInterviewEventProcessor implements SQSMessageHandler {
	private final AmazonSQS amazonSQS;
	private final ObjectMapper objectMapper;
	private final StaticAppConfigValues appConfigValues;
	private final BgsDataGenerator bgsDataGenerator;

	@Override
	public void handle(final Message message) throws Exception {
		final InterviewFeedbackSubmittedEvent event = this.objectMapper.readValue(message.getBody(),
				InterviewFeedbackSubmittedEvent.class);
		try {
			log.info("Processing the interview {}", event.getInterviewId());
			this.bgsDataGenerator.generateBgsDataForInterview(event.getInterviewId());
			this.amazonSQS.deleteMessage(new DeleteMessageRequest(
					this.appConfigValues.getFeedbackSubmittedEventQueueUrl(), message.getReceiptHandle()));
		} catch (final Exception ex) {
			log.error(ex);
			log.error(String.format("Error in processing sentiment of feedback for interview id- %s",
					event.getInterviewId()));
		}
	}

	@Override
	public String queueUrl() {
		return this.appConfigValues.getFeedbackSubmittedEventQueueUrl();
	}
}
