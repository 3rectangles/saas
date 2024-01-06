/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interviewing;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.feedbacksubmittedevent.FeedbackSubmittedEvent;
import com.barraiser.onboarding.events.InterviewingConsumer;
import com.barraiser.onboarding.interviewing.notes.InterviewingDataSaver;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class FeedbackSubmittedEventHandler implements EventListener<InterviewingConsumer> {
	private final InterviewingDataSaver interviewingDataSaver;
	private final ObjectMapper objectMapper;

	@Override
	public List<Class> eventsToListen() {
		return List.of(FeedbackSubmittedEvent.class);
	}

	@Override
	public void handleEvent(Event event) throws Exception {
		final FeedbackSubmittedEvent feedbackSubmittedEvent = this.objectMapper.convertValue(event.getPayload(),
				FeedbackSubmittedEvent.class);

		this.interviewingDataSaver.saveInterviewingData(feedbackSubmittedEvent.getInterview().getId());
	}
}
