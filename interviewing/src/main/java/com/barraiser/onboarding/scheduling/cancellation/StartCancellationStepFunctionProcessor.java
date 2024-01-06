/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.interview.InterviewServiceDeskEventProcessor;
import com.barraiser.onboarding.payment.expert.InterviewServiceDeskProcessingData;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class StartCancellationStepFunctionProcessor implements InterviewServiceDeskEventProcessor {

	private final InterviewCancellationManager interviewCancellationManager;
	private final static String cancelledBy = "BarRaiser";

	@Override
	public void process(final InterviewServiceDeskProcessingData data) throws Exception {
		// Leaving source here as null.
		this.interviewCancellationManager.cancel(data.getInterview(), cancelledBy, null);
	}

}
