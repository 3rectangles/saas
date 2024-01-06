/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations.lever;

import com.barraiser.ats_integrations.lever.LeverCandidateAdditionHandler;
import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.candidateaddition.CandidateAddition;
import com.barraiser.onboarding.events.InterviewingConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
public class LeverCandidateAdditionEventHandler implements EventListener<InterviewingConsumer> {
	private final ObjectMapper objectMapper;
	private final LeverCandidateAdditionHandler leverCandidateAdditionHandler;

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

		try {
			this.leverCandidateAdditionHandler
					.sendPartnerPortalLinkToLever(candidateAddition);

			this.leverCandidateAdditionHandler
					.addBarRaiserEvaluationStartedTagToLever(candidateAddition);
		} catch (Exception ex) {
			log.error("Unable to send tag and link to Lever", ex);
		}
	}
}
