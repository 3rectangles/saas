/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.onboarding.events.InterviewingConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class GenerateHighlightEventHandler implements EventListener<InterviewingConsumer> {

	@Override
	public List<Class> eventsToListen() {
		return null;
		// return List.of(GenerateHighlightEvent.class);
	}

	@Override
	public void handleEvent(Event event) throws Exception {

	}
}
