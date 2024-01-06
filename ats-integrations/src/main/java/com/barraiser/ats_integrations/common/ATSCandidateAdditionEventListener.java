/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.common;

import com.barraiser.ats_integrations.events.ATSIntegrationsConsumer;
import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.candidateaddition.CandidateAddition;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class ATSCandidateAdditionEventListener implements EventListener<ATSIntegrationsConsumer> {
	private final ObjectMapper objectMapper;
	private final ATSCandidateAdditionManager atsCandidateAdditionManager;

	@Override
	public List<Class> eventsToListen() {
		return List.of(CandidateAddition.class);
	}

	@Override
	public void handleEvent(Event event) throws Exception {
		final CandidateAddition candidateAddition = this.objectMapper
				.convertValue(
						event.getPayload(),
						CandidateAddition.class);

		log.info(String.format(
				"Performing necessary operations upon CandidateAddition for evaluationId:%s",
				candidateAddition
						.getCandidate()
						.getEvaluationId()));

		try {
			this.atsCandidateAdditionManager
					.performNecessaryOperationsUponCandidateAddition(candidateAddition
							.getCandidate()
							.getEvaluationId());

		} catch (Exception exception) {
			log.error(String.format(
					"Unable to perform necessary operations upon CandidateAddition for evaluationId:%s",
					candidateAddition
							.getCandidate()
							.getEvaluationId()),
					exception);
		}
	}
}
