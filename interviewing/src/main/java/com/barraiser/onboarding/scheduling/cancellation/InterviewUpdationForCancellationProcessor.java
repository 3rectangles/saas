/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@AllArgsConstructor
@Component
public class InterviewUpdationForCancellationProcessor implements CancellationProcessor {

	private final InterViewRepository interViewRepository;
	private final InterviewService interviewService;

	@Override
	public void process(final CancellationProcessingData data) {
		final InterviewDAO savedInterview = this.interViewRepository.findById(data.getInterviewId()).get();
		final InterviewDAO updatedInterview = savedInterview.toBuilder()
				.status(InterviewStatus.CANCELLATION_DONE.getValue())
				.cancellationTime(data.getInterviewToBeCancelled().getCancellationTime())
				.cancellationReasonId(data.getInterviewToBeCancelled().getCancellationReasonId())
				.build();
		data.setInterviewToBeCancelled(this.interviewService.save(updatedInterview,
				data.getUserCancellingTheInterview(),
				data.getSourceOfCancellation()));
		data.setPreviousStateOfCancelledInterview(data.getInterviewToBeCancelled().toBuilder().build());
	}
}
