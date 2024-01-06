/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.candidateaddition.CandidateAddition;
import com.barraiser.communication.automation.dal.WhatsAppConsentDAO;
import com.barraiser.communication.automation.dal.WhatsAppConsentRepository;
import com.barraiser.onboarding.events.InterviewingConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Log4j2
@RequiredArgsConstructor
public class WhatsappConsentUpdationHandler implements EventListener<InterviewingConsumer> {
	private final ObjectMapper objectMapper;
	private final WhatsAppConsentRepository whatsAppConsentRepository;

	@Override
	public List<Class> eventsToListen() {
		return List.of(CandidateAddition.class);
	}

	@Override
	public void handleEvent(final Event event) throws Exception {
		final CandidateAddition candidateAdditionEvent = this.objectMapper.convertValue(event.getPayload(),
				CandidateAddition.class);
		this.updateCandidateConsent(candidateAdditionEvent);
	}

	private void updateCandidateConsent(final CandidateAddition candidateAddition) {
		final Optional<WhatsAppConsentDAO> whatsAppConsentDAO = this.whatsAppConsentRepository
				.findByPhone(candidateAddition.getCandidate().getPhone());
		if (whatsAppConsentDAO.isEmpty()) {
			this.whatsAppConsentRepository.save(WhatsAppConsentDAO.builder().id(UUID.randomUUID().toString())
					.phone(candidateAddition.getCandidate().getPhone()).consent(Boolean.TRUE).build());
		}
	}
}
