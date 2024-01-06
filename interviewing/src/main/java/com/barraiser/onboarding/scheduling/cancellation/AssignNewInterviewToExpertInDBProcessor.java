/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterviewService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@AllArgsConstructor
@Component
public class AssignNewInterviewToExpertInDBProcessor implements CancellationProcessor {
	private final InterviewService interviewService;

	@Override
	public void process(final CancellationProcessingData data) throws Exception {
		this.changeInterviewerForInterview(data);
	}

	private void changeInterviewerForInterview(final CancellationProcessingData data) {
		InterviewDAO interviewDAO = data.getInterviewThatExpertCanTake();
		final String interviewerId = data.getPreviousStateOfCancelledInterview().getInterviewerId();
		interviewDAO = this.interviewService.save(interviewDAO.toBuilder().interviewerId(interviewerId).build());
		data.setInterviewThatExpertCanTake(interviewDAO);
	}
}
