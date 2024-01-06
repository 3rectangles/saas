/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.confirmation;

import com.barraiser.onboarding.dal.InterviewConfirmationDAO;
import com.barraiser.onboarding.dal.InterviewConfirmationRepository;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.scheduling.lifecycle.DTO.InterviewConfirmationStatus;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class InterviewConfirmationManager {
	private final InterViewRepository interViewRepository;
	private final InterviewConfirmationRepository interviewConfirmationRepository;

	public String getInterviewConfirmationStatus(final String interviewId) {
		final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId).get();
		return this.getInterviewConfirmationStatus(interviewId, interviewDAO.getRescheduleCount());
	}

	public String getInterviewConfirmationStatus(final String interviewId, final Integer rescheduleCount) {
		final Optional<InterviewConfirmationDAO> interviewConfirmationDAOOptional = this.interviewConfirmationRepository
				.findTopByInterviewIdAndCandidateConfirmationTimeNotNullAndRescheduleCountOrderByCandidateConfirmationTimeDesc(
						interviewId, rescheduleCount);

		final InterviewConfirmationStatus interviewConfirmationStatus = interviewConfirmationDAOOptional
				.map(interviewConfirmationDAO -> interviewConfirmationDAO.getCandidateConfirmation()
						? InterviewConfirmationStatus.CONFIRMED
						: InterviewConfirmationStatus.DENIED)
				.orElse(InterviewConfirmationStatus.NOACTION);

		return interviewConfirmationStatus.name();
	}
}
