/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.events;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.EventParser;
import com.barraiser.onboarding.dal.EventLogsDAO;
import com.barraiser.onboarding.dal.EventLogsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@RequiredArgsConstructor
public class DBLogEventListener implements EventListener<DBLogEventsConsumer> {
	private final EventLogsRepository eventLogsRepository;

	@Override
	public List<Class> eventsToListen() {
		return null;
	}

	@Override
	public void handleEvent(Event event) throws Exception {
		final EventParser eventParser = new EventParser();
		this.eventLogsRepository.save(EventLogsDAO.builder()
				.id(event.getId())
				.timestamp(event.getTimestamp())
				.source(event.getSource())
				.type(event.getEventType())
				.payload(eventParser.serialize(event.getPayload()))
				.version(event.getVersion())
				.build());
	}
}
