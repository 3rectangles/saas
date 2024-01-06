/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment.sfn_activities;

import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertAllocatorData;
import com.barraiser.onboarding.sfn.async.StepFunction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sfn.SfnAsyncClient;

import java.util.List;

@Component
public class ExpertAllocatorStepFunction extends StepFunction<ExpertAllocatorData> {

	public ExpertAllocatorStepFunction(
			final List<ExpertAllocatorSfnActivity> activities,
			final SfnAsyncClient sfnAsyncClient,
			final ObjectMapper objectMapper,
			final Environment environment) {
		super(activities, sfnAsyncClient, objectMapper, environment);
	}
}
