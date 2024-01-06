/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interviewing.step_function;

import com.barraiser.onboarding.interviewing.step_function.dto.InterviewingLifecycleDTO;
import com.barraiser.onboarding.sfn.async.StepFunction;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sfn.SfnAsyncClient;

import java.util.List;

@Component
public class InterviewingLifecycleStepFunction extends StepFunction<InterviewingLifecycleDTO> {

	public InterviewingLifecycleStepFunction(
			final List<InterviewingLifecycleSfnActivity> activities,
			final SfnAsyncClient sfnAsyncClient,
			final ObjectMapper objectMapper,
			final Environment environment) {
		super(activities, sfnAsyncClient, objectMapper, environment);
	}
}
