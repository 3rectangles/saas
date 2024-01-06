/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.interviewscreatedevent.InterviewsCreatedEvent;
import com.barraiser.commons.eventing.schema.commons.InterviewDetailEvent;
import com.barraiser.onboarding.events.InterviewingEventProducer;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@AllArgsConstructor
public class InterviewsCreatedEventGenerator {
	private final InterviewingEventProducer eventProducer;

	public void sendInterviewsCreatedEvent(final String evaluationId, final List<String> interviewIds) {
		final Event<InterviewsCreatedEvent> event = this.createEventData(evaluationId, interviewIds);
		try {
			this.eventProducer.pushEvent(event);
		} catch (final Exception err) {
			log.error(err, err);
		}
	}

	private Event<InterviewsCreatedEvent> createEventData(final String evaluationId, final List<String> interviewIds) {
		final List<InterviewDetailEvent> interviewDetails = new ArrayList<>();
		interviewIds.forEach(x -> interviewDetails.add(new InterviewDetailEvent().id(x).evaluationId(evaluationId)));
		final Event<InterviewsCreatedEvent> event = new Event<>();
		event.setPayload(new InterviewsCreatedEvent().interviews(interviewDetails));
		return event;
	}

}
