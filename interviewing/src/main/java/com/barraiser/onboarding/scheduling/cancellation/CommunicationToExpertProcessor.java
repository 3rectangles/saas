/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.communication.InterviewCancellationCommunicationService;
import com.barraiser.onboarding.communication.InterviewReassignedToExpertCommunicationService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@AllArgsConstructor
@Component
public class CommunicationToExpertProcessor implements CancellationProcessor {
	private final InterviewCancellationCommunicationService interviewCancellationCommunicationService;
	private final InterviewReassignedToExpertCommunicationService interviewReassignedToExpertCommunicationService;

	@Override
	public void process(final CancellationProcessingData data) throws Exception {
		if (this.shouldExpertGetReschedulingMail(data)) {
			this.interviewReassignedToExpertCommunicationService.communicateInterviewUpdationToExpert(
					data.getInterviewThatExpertCanTake(),
					data.getPreviousStateOfCancelledInterview().getInterviewerId());
		} else {
			this.interviewCancellationCommunicationService.communicateCancellationToExpert(
					data.getPreviousStateOfCancelledInterview(),
					data.getPreviousStateOfCancelledInterview().getInterviewerId());
		}
	}

	private boolean shouldExpertGetReschedulingMail(final CancellationProcessingData data) {
		return data.getInterviewThatExpertCanTake() != null;
	}
}
