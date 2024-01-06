/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.expert;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.interviewcompletion.InterviewCompletion;
import com.barraiser.onboarding.dal.ExpertDAO;
import com.barraiser.onboarding.dal.ExpertRepository;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.events.InterviewingConsumer;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@RequiredArgsConstructor
public class TotalInterviewsCalculationHandler implements EventListener<InterviewingConsumer> {
	private final ObjectMapper objectMapper;
	private final InterViewRepository interViewRepository;
	private final ExpertRepository expertRepository;

	@Override
	public List<Class> eventsToListen() {
		return List.of(InterviewCompletion.class);
	}

	@Override
	public void handleEvent(final Event event) throws Exception {
		final InterviewCompletion interviewCompletionEvent = this.objectMapper.convertValue(event.getPayload(),
				InterviewCompletion.class);
		this.calculateTotalInterviewsTakenByExpert(interviewCompletionEvent);
	}

	private void calculateTotalInterviewsTakenByExpert(final InterviewCompletion interviewCompletionEvent) {
		final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewCompletionEvent
				.getInterview().getId()).get();
		final ExpertDAO interviewer = this.expertRepository.findById(interviewDAO.getInterviewerId()).get();
		this.expertRepository
				.save(interviewer.toBuilder().totalInterviewsCompleted(interviewer.getTotalInterviewsCompleted() + 1)
						.build());
	}
}
