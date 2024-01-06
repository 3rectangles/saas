/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations.lever;

import com.barraiser.ats_integrations.lever.LeverEvaluationCompletedHandler;
import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.evaluationcompleted.EvaluationCompleted;
import com.barraiser.onboarding.events.InterviewingConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class LeverEvaluationCompletedEventHandler
		implements EventListener<InterviewingConsumer> {
	private final ObjectMapper objectMapper;
	private final LeverEvaluationCompletedHandler leverEvaluationCompletedHandler;

	@Override
	public List<Class> eventsToListen() {
		return List.of(EvaluationCompleted.class);
	}

	@Override
	public void handleEvent(Event event) throws Exception {
		final EvaluationCompleted evaluationCompleted = this.objectMapper.convertValue(
				event.getPayload(),
				EvaluationCompleted.class);

		log.info("Sending evaluation completed event for sending candidate evaluation link to lever");

		try {
			this.leverEvaluationCompletedHandler
					.sendCandidateEvaluationLinkToLever(evaluationCompleted);

			this.leverEvaluationCompletedHandler
					.addBarRaiserEvaluationCompletedTagToLever(evaluationCompleted);
		} catch (Exception ex) {
			log.error("Unable to send tag and link to Lever", ex);
		}
	}
}
