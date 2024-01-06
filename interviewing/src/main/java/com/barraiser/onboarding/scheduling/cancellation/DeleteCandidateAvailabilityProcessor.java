/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.availability.CandidateAvailabilityManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@AllArgsConstructor
@Component
public class DeleteCandidateAvailabilityProcessor implements CancellationProcessor {
	private final CandidateAvailabilityManager candidateAvailabilityManager;

	@Override
	public void process(final CancellationProcessingData data) throws Exception {
		this.candidateAvailabilityManager
				.deletePreviousAvailabilityGivenByCandidate(data.getInterviewToBeCancelled().getId());
	}
}
