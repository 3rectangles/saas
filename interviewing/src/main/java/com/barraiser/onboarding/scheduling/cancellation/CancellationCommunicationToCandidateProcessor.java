/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.communication.InterviewCancellationCommunicationService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class CancellationCommunicationToCandidateProcessor implements CancellationProcessor {

	private final InterviewCancellationCommunicationService interviewCancellationCommunicationService;

	@Override
	public void process(final CancellationProcessingData data) throws Exception {
		this.interviewCancellationCommunicationService
				.communicateCancellationToCandidate(data.getPreviousStateOfCancelledInterview());
	}

}
