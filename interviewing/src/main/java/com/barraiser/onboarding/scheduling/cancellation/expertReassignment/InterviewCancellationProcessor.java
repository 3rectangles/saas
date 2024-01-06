/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment;

import com.barraiser.onboarding.scheduling.cancellation.InterviewCancellationManager;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertReassignmentData;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@Component
public class InterviewCancellationProcessor implements ExpertReassignmentProcessor {
	private final InterviewCancellationManager interviewCancellationManager;

	@Override
	public void process(final ExpertReassignmentData data) throws Exception {
		this.interviewCancellationManager.cancel(data.getInterview()
				.toBuilder().cancellationTime(data.getCancellationRequestedTimeOfInterview().toString())
				.cancellationReasonId(data.getReassignmentReason()).build(), data.getReassignedBy(), null);
	}
}
