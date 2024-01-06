/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.recommendation;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.evaluationcompleted.EvaluationCompleted;
import com.barraiser.commons.eventing.schema.commons.Evaluation;
import com.barraiser.onboarding.events.InterviewingConsumer;
import com.barraiser.onboarding.partner.PartnerConfigurationManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
public class EvaluationRecommendationGenerationHandler
		implements EventListener<InterviewingConsumer> {
	private final ObjectMapper objectMapper;
	private final EvaluationRecommendationGenerator evaluationRecommendationGenerator;
	private final PartnerConfigurationManager partnerConfigurationManager;

	@Override
	public List<Class> eventsToListen() {
		return List.of(EvaluationCompleted.class);
	}

	@Override
	public void handleEvent(final Event event) throws Exception {
		final EvaluationCompleted evaluationCompleted = this.objectMapper
				.convertValue(
						event.getPayload(),
						EvaluationCompleted.class);

		final Evaluation evaluation = evaluationCompleted.getEvaluation();
		if (partnerConfigurationManager.isRecommendationEnabled(evaluation.getPartnerId())) {
			log.info(String.format(
					"Generating evaluation recommendation for evaluationId:%s and saving it in database",
					evaluationCompleted
							.getEvaluation()
							.getId()));
			this.evaluationRecommendationGenerator
					.generateEvaluationRecommendationAndSaveToDatabase(
							evaluationCompleted
									.getEvaluation()
									.getId());
		} else {
			log.info(String.format(
					"Not Generating evaluation recommendation for evaluationId:%s ",
					evaluationCompleted
							.getEvaluation()
							.getId()));
		}
	}
}
