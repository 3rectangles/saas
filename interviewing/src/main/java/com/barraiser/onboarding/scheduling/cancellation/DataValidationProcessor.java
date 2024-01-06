/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.dal.CancellationReasonRepository;
import com.barraiser.onboarding.dal.CandidateDAO;
import com.barraiser.onboarding.interview.InterViewRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component("cancellationDataValidationProcessor")
@AllArgsConstructor
public class DataValidationProcessor implements CancellationProcessor {

	private final InterViewRepository interViewRepository;
	private final CandidateInformationManager candidateInformationManager;
	private final CancellationReasonRepository cancellationReasonRepository;

	@Override
	public void process(final CancellationProcessingData data) {
		this.interViewRepository.findById(data.getInterviewId()).orElseThrow(
				() -> new IllegalArgumentException("No interview found with id :" + data.getInterviewId()));

		final CandidateDAO candidateDAO = this.candidateInformationManager
				.getCandidate(data.getInterviewToBeCancelled().getIntervieweeId());
		if (candidateDAO == null) {
			throw new IllegalArgumentException("Interviewee does not exist.");
		}

		this.cancellationReasonRepository.findById(data.getInterviewToBeCancelled().getCancellationReasonId())
				.orElseThrow(() -> new IllegalArgumentException("Cancellation reason does not exist."));
	}

}
