/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.sfn;

import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import com.barraiser.onboarding.sfn.async.StepFunction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sfn.SfnAsyncClient;

import java.util.List;

@Component
public class ScheduleInterviewStepFunction extends StepFunction<SchedulingProcessingData> {

	public ScheduleInterviewStepFunction(
			final List<InterviewSchedulingActivity> activities,
			final SfnAsyncClient sfnAsyncClient,
			final ObjectMapper objectMapper,
			final Environment environment) {
		super(activities, sfnAsyncClient, objectMapper, environment);
	}
}
