/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation;

import com.barraiser.communication.automation.constants.Status;
import com.barraiser.communication.automation.dal.CommunicationLogDAO;
import com.barraiser.communication.automation.dal.CommunicationLogRepository;
import com.barraiser.communication.automation.pipeline.pojo.CommunicationInput;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Log4j2
@RequiredArgsConstructor
@Component
public class CommunicationLogger {
	private final CommunicationLogRepository communicationLogRepository;

	public void logSuccess(final CommunicationInput communicationInput, final JsonNode payload) {
		this.communicationLogRepository.save(this.constructBaseLogDAO(communicationInput).toBuilder()
				.status(Status.SUCCESS)
				.payload(payload)
				.build());
	}

	public void logFailure(final CommunicationInput communicationInput) {
		this.communicationLogRepository.save(this.constructBaseLogDAO(communicationInput).toBuilder()
				.status(Status.FAILED)
				.build());
	}

	public void logSkipped(final CommunicationInput communicationInput) {
		this.communicationLogRepository.save(this.constructBaseLogDAO(communicationInput).toBuilder()
				.status(Status.SKIPPED)
				.build());
	}

	private CommunicationLogDAO constructBaseLogDAO(final CommunicationInput communicationInput) {
		return CommunicationLogDAO.builder()
				.id(UUID.randomUUID().toString())
				.channel(communicationInput.getChannel())
				.eventType(communicationInput.getEventType())
				.entityId(communicationInput.getEntity().getId())
				.entityType(communicationInput.getEntity().getType())
				.partnerId(communicationInput.getEntity().getPartnerId())
				.recipientType(communicationInput.getRecipientType())
				.build();
	}
}
