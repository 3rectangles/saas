/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.pipeline;

import com.barraiser.common.entity.Entity;
import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.communication.CommunicationConsumer;
import com.barraiser.communication.automation.CommunicationLogger;
import com.barraiser.communication.automation.pipeline.exception.SkipCommunicationException;
import com.barraiser.communication.automation.pipeline.pojo.CommunicationInput;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class CommunicationHandler implements EventListener<CommunicationConsumer> {
	private final EventEntityExtractor eventEntityExtractor;
	private final CommunicationInputsFetcher communicationInputsFetcher;
	private final List<CommunicationOrchestrator> communicationOrchestrators;
	private final CommunicationLogger communicationLogger;
	private final ObjectMapper objectMapper;

	@Override
	public List<Class> eventsToListen() {
		return null;
	}

	@Override
	public void handleEvent(Event event) throws Exception {
		final Entity entity = this.eventEntityExtractor.getEntity(event);
		// If there is no mapping of event to entity, we can assume communication is not
		// needed for this event
		if (entity == null) {
			return;
		}

		final List<CommunicationInput> inputs = this.communicationInputsFetcher
				.getInputs(
						event.getEventType(),
						event.getPayload(),
						entity);

		for (final CommunicationInput input : inputs) {
			this.communicate(input);
		}
	}

	private void communicate(final CommunicationInput input) throws JsonProcessingException {
		log.info("starting communication : {}", this.objectMapper.writeValueAsString(input));
		for (final CommunicationOrchestrator communicationOrchestrator : this.communicationOrchestrators) {
			if (communicationOrchestrator.getChannel().equals(input.getChannel())) {
				try {
					final JsonNode communicationPayload = communicationOrchestrator.communicate(input);
					this.communicationLogger.logSuccess(input, communicationPayload);
				} catch (final SkipCommunicationException e) {
					log.info("communication skipped : " + e.getMessage(), e);
					this.communicationLogger.logSkipped(input);
				} catch (final Exception e) {
					log.error("communication failed : " + this.objectMapper.writeValueAsString(input), e);
					this.communicationLogger.logFailure(input);
				}
				return;
			}
		}
	}
}
