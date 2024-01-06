/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.followup;

import com.barraiser.onboarding.interview.EvaluationServiceDeskEventProcessor;
import com.barraiser.onboarding.interview.jira.evaluation.EvaluationServiceDeskProcessingData;
import com.barraiser.onboarding.scheduling.followup.FollowUpForSchedulingManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class StartFollowUpForSchedulingStepFunctionProcessor implements EvaluationServiceDeskEventProcessor {

	private final FollowUpForSchedulingManager followUpForSchedulingManager;

	@Override
	public void process(final EvaluationServiceDeskProcessingData data) throws Exception {
		this.followUpForSchedulingManager.trigger(data);
	}

}
