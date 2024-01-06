/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.availabilitychangeevent.AvailabilityChangeEvent;
import com.barraiser.onboarding.communication.AddAvailabilityCommunicationService;
import com.barraiser.onboarding.events.InterviewingConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Log4j2
@RequiredArgsConstructor
public class AvailabilityChangeEventHandler implements EventListener<InterviewingConsumer> {
	private final ObjectMapper objectMapper;
	private final AddAvailabilityCommunicationService addAvailabilityCommunicationService;

	@Override
	public List<Class> eventsToListen() {
		return List.of(AvailabilityChangeEvent.class);
	}

	@Override
	public void handleEvent(final Event event) throws Exception {
		final AvailabilityChangeEvent addAvailabilityEvent = this.objectMapper.convertValue(event.getPayload(),
				AvailabilityChangeEvent.class);
		this.communicateChangeOfAvailability(addAvailabilityEvent);
	}

	private void communicateChangeOfAvailability(final AvailabilityChangeEvent event)
			throws Exception {
		final List<AvailabilityInput> input = event.getSlots().stream()
				.map(x -> this.objectMapper.convertValue(x, AvailabilityInput.class))
				.collect(Collectors.toList());
		if (!"INTERVIEW".equals(event.getContextType())) {
			this.addAvailabilityCommunicationService.communicateAdditionOfAvailabilityOfExpert(
					input.stream()
							.map(x -> this.objectMapper.convertValue(x, AvailabilitySlot.class))
							.collect(Collectors.toList()),
					event.getUserId());
		} else {
			this.addAvailabilityCommunicationService.communicateChangeOfAvailabilityOfCandidate(
					event.getContextId(),
					event.getSlots().stream()
							.map(x -> this.objectMapper.convertValue(x, AvailabilitySlot.class))
							.collect(Collectors.toList()));
		}
	}
}
