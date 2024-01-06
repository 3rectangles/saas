/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.interviewcompletion.InterviewCompletion;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.events.InterviewingConsumer;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@Log4j2
@RequiredArgsConstructor
public class NextRoundCreationHandler implements EventListener<InterviewingConsumer> {
	private final InterviewUtil interviewUtil;
	private final NextInterviewRoundActionProcessor nextInterviewRoundActionProcessor;
	private final ObjectMapper objectMapper;
	private final InterViewRepository interViewRepository;
	private final EvaluationRepository evaluationRepository;

	@Override
	public List<Class> eventsToListen() {
		return List.of(InterviewCompletion.class);
	}

	@Override
	public void handleEvent(final Event event) throws Exception {
		final InterviewCompletion interviewCompletionEvent = this.objectMapper.convertValue(event.getPayload(),
				InterviewCompletion.class);
		this.handleNextRoundsCreation(interviewCompletionEvent);
	}

	private void handleNextRoundsCreation(final InterviewCompletion interviewCompletionEvent)
			throws ParseException {
		final Optional<InterviewDAO> interviewDAO = this.interViewRepository
				.findById(interviewCompletionEvent.getInterview().getId());
		final Optional<EvaluationDAO> evaluationDAO = this.evaluationRepository
				.findById(interviewDAO.get().getEvaluationId());
		if (this.interviewUtil.isNextRoundCreationDependent(
				evaluationDAO.get().getJobRoleId(),
				evaluationDAO.get().getJobRoleVersion(),
				interviewDAO.get().getInterviewStructureId())) {
			this.nextInterviewRoundActionProcessor.takeActionForNextRound(interviewDAO.get());
		}
	}
}
