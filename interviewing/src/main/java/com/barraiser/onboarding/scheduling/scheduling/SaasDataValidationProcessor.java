/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling;

import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessor;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Log4j2
@Qualifier("SaasSchedulingDataValidationProcessor")
@Component("SaasSchedulingDataValidationProcessor")
@AllArgsConstructor
public class SaasDataValidationProcessor implements SchedulingProcessor {
	private static final String INTERVIEW_STATUS_PENDING_SCHEDULING = "pending_scheduling";

	private final InterViewRepository interViewRepository;
	private final ExpertRepository expertRepository;
	private final EvaluationRepository evaluationRepository;

	@Override
	public void process(final SchedulingProcessingData data) {
		final InterviewDAO interview = this.interViewRepository
				.findById(data.getInput().getInterviewId())
				.orElseThrow(() -> new IllegalArgumentException(
						String.format("Interview does not exist for id: %s", data.getInput().getInterviewId())));

		this.expertRepository
				.findById(data.getInput().getInterviewerId())
				.orElseThrow(() -> new IllegalArgumentException(String.format(
						"Interviewer does not exist for interviewer_id: %s", data.getInput().getInterviewerId())));

		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(interview.getEvaluationId())
				.orElseThrow(() -> new IllegalArgumentException(
						String.format("Evaluation does not exist for evaluation_id: %s", interview.getEvaluationId())));

		if (EvaluationStatus.CANCELLED.getValue().equalsIgnoreCase(evaluationDAO.getStatus())) {
			throw new IllegalArgumentException("Evaluation has been cancelled, so scheduling is not possible.");
		}

		if (INTERVIEW_STATUS_PENDING_SCHEDULING.equals(interview.getStatus())
				|| InterviewStatus.SLOT_REQUESTED_BY_CANDIDATE.getValue().equals(interview.getStatus())) {
			if (interview.getStartDate() != null || interview.getInterviewerId() != null) {
				throw new IllegalArgumentException("Interview is already scheduled, please contact support.");
			}
		} else {
			throw new IllegalArgumentException("Interview cannot be scheduled, please contact support.");
		}

	}
}
