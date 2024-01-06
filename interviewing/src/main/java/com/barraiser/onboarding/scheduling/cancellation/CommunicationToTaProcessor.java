/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.communication.InterviewCancellationCommunicationService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@AllArgsConstructor
@Component
public class CommunicationToTaProcessor implements CancellationProcessor {
	private final InterviewCancellationCommunicationService interviewCancellationCommunicationService;

	@Override
	public void process(final CancellationProcessingData data) throws Exception {
		if (this.shouldTaGetReschedulingMail(data)) {
			this.interviewCancellationCommunicationService.communicateInterviewUpdationToTa(
					data.getInterviewForTaReassignment(),
					data.getPreviousStateOfCancelledInterview().getTaggingAgent());
		} else if (data.getIsTaAssigned()) {
			this.interviewCancellationCommunicationService
					.sendEmailToTaOnCancellation(data.getPreviousStateOfCancelledInterview());
		}
	}

	private boolean shouldTaGetReschedulingMail(final CancellationProcessingData data) {
		return data.getInterviewForTaReassignment() != null && Boolean.TRUE.equals(data.getIsTaAutoAllocationEnabled());
	}
}
