/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.payment.expert;

import com.amazonaws.services.sqs.model.Message;
import com.barraiser.commons.eventing.sqs.SQSMessageHandler;
import com.barraiser.onboarding.common.StaticAppConfigValues;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class ExpertPaymentEventQueueProcessor implements SQSMessageHandler {

	private final ExpertPaymentManager expertPaymentManager;

	private final ObjectMapper objectMapper;
	private final StaticAppConfigValues staticAppConfigValues;

	@Override
	public void handle(final Message message) throws Exception {
		log.info("Expert Payment data received : {}", message.getBody());
		final InterviewConcludedEvent interviewEvent = this.objectMapper.readValue(message.getBody(),
				InterviewConcludedEvent.class);
		final InterviewPaymentCalculationData data = this.objectMapper.convertValue(interviewEvent,
				InterviewPaymentCalculationData.class);
		this.expertPaymentManager.computeAndSave(data);
	}

	@Override
	public String queueUrl() {
		return this.staticAppConfigValues.getExpertPaymentCalculationEventQueueUrl();
	}
}
