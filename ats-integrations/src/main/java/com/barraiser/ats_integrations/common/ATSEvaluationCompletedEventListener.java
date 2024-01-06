/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common;

import com.barraiser.ats_integrations.events.ATSIntegrationsConsumer;
import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.evaluationcompleted.EvaluationCompleted;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class ATSEvaluationCompletedEventListener implements EventListener<ATSIntegrationsConsumer> {
	private final ObjectMapper objectMapper;
	private final ATSEvaluationCompletedManager atsEvaluationCompletedManager;

	@Override
	public List<Class> eventsToListen() {
		return List.of(EvaluationCompleted.class);
	}

	@Override
	public void handleEvent(Event event) throws Exception {
		final EvaluationCompleted evaluationCompleted = this.objectMapper
				.convertValue(
						event.getPayload(),
						EvaluationCompleted.class);

		log.info(String.format(
				"Performing necessary operations upon EvaluationCompleted evaluationId:%s",
				evaluationCompleted.getEvaluation().getId()));

		try {
			this.atsEvaluationCompletedManager
					.performNecessaryOperationsUponEvaluationCompletion(evaluationCompleted
							.getEvaluation()
							.getId());
		} catch (Exception exception) {
			log.error(String.format(
					"Unable to perform necessary operations upon EvaluationCompletion for evaluationId:%s",
					evaluationCompleted
							.getEvaluation()
							.getId()),
					exception);
		}
	}
}
