/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.interviewscreatedevent.InterviewsCreatedEvent;
import com.barraiser.commons.eventing.schema.commons.InterviewDetailEvent;
import com.barraiser.onboarding.common.Constants;
import com.barraiser.onboarding.communication.InterviewCreationCommunicationService;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.events.InterviewingConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Log4j2
@RequiredArgsConstructor
public class InterviewsCreationHandler implements EventListener<InterviewingConsumer> {
	private final ObjectMapper objectMapper;
	private final InterViewRepository interViewRepository;
	private final EvaluationRepository evaluationRepository;
	private final InterviewCreationCommunicationService interviewCreationCommunicationService;

	@Override
	public List<Class> eventsToListen() {
		return List.of(InterviewsCreatedEvent.class);
	}

	@Override
	public void handleEvent(final Event event) throws Exception {
		final InterviewsCreatedEvent interviewsCreatedEvent = this.objectMapper.convertValue(event.getPayload(),
				InterviewsCreatedEvent.class);
		this.communicateCreationOfInterviews(interviewsCreatedEvent);
	}

	private void communicateCreationOfInterviews(final InterviewsCreatedEvent interviewsCreatedEvent)
			throws IOException {
		final List<InterviewDAO> interviews = this.interViewRepository
				.findAllByIdIn(interviewsCreatedEvent.getInterviews()
						.stream().map(InterviewDetailEvent::getId).collect(Collectors.toList()));
		final List<InterviewDAO> sortedInterviews = interviewsCreatedEvent.getInterviews().stream()
				.map(x -> interviews.stream().filter(y -> y.getId().equals(x.getId())).findFirst().get())
				.collect(Collectors.toList());
		final Boolean isRoundInternal = this.checkIfRoundInternal(sortedInterviews);
		if (isRoundInternal)
			return;
		final EvaluationDAO evaluationDAO = this.evaluationRepository
				.findById(interviewsCreatedEvent.getInterviews().get(0).getEvaluationId()).get();
		this.interviewCreationCommunicationService.sendEmailRegardingInterviewCreation(evaluationDAO, sortedInterviews);
	}

	private Boolean checkIfRoundInternal(List<InterviewDAO> sortedInterviews) {
		return sortedInterviews != null && !sortedInterviews.isEmpty()
				&& sortedInterviews.get(0).getInterviewRound().equals(Constants.ROUND_TYPE_INTERNAL);
	}
}
