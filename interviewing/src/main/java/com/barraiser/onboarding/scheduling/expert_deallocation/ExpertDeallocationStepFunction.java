/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.expert_deallocation;

import com.barraiser.onboarding.scheduling.expert_deallocation.dto.ExpertDeAllocatorData;
import com.barraiser.onboarding.sfn.async.StepFunction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sfn.SfnAsyncClient;

import java.util.List;

@Component
public class ExpertDeallocationStepFunction extends StepFunction<ExpertDeAllocatorData> {

	public ExpertDeallocationStepFunction(
			final List<ExpertDeallocationSfnActivity> activities,
			final SfnAsyncClient sfnAsyncClient,
			final ObjectMapper objectMapper,
			final Environment environment) {
		super(activities, sfnAsyncClient, objectMapper, environment);
	}
}
