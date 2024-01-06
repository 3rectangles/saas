/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.expert_deallocation.step_function;

import com.barraiser.onboarding.expert.ExpertReminderData;
import com.barraiser.onboarding.expert.ExpertReminderSfnActivity;
import com.barraiser.onboarding.sfn.async.StepFunction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sfn.SfnAsyncClient;

import java.util.List;

@Component
public class ExpertReminderLifecycleStepFunction extends StepFunction<ExpertReminderData> {
	public ExpertReminderLifecycleStepFunction(final List<ExpertReminderSfnActivity> stepFunctionActivities,
			final SfnAsyncClient sfnAsyncClient, final ObjectMapper objectMapper, final Environment environment) {
		super(stepFunctionActivities, sfnAsyncClient, objectMapper, environment);
	}
}
