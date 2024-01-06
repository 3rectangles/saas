/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.sfn;

import com.amazonaws.services.stepfunctions.model.GetActivityTaskResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class StepFunctionTaskProcessor<T> {

	private final ObjectMapper objectMapper;
	private final StepFunctionManager stepFunctionManager;

	public void handle(final String activityName, final StepFunctionProcessor<T> stepFunctionProcessor,
			Class<T> stepFunctionDtoClass)
			throws Exception {
		final GetActivityTaskResult getActivityTaskResultResponse = this.stepFunctionManager
				.getGetActivityTaskResult(activityName);
		if (getActivityTaskResultResponse == null || getActivityTaskResultResponse.getTaskToken() == null)
			return;
		log.info("Received task for activity " + activityName);
		String flowIdentifier = null;

		try {
			T stepFunctionDto = this.stepFunctionManager.getStepInput(
					getActivityTaskResultResponse,
					stepFunctionDtoClass);
			flowIdentifier = stepFunctionProcessor.getFlowIdentifier(stepFunctionDto);
			stepFunctionProcessor.process(stepFunctionDto);
			final String stepResponse = this.objectMapper.writeValueAsString(stepFunctionDto);
			this.stepFunctionManager.acknowledgeStepSuccess(
					getActivityTaskResultResponse.getTaskToken(),
					stepResponse, flowIdentifier);
		} catch (final Exception e) {
			log.info(
					"The was an exception while processing activity {} for flowIdentifier {}",
					activityName,
					flowIdentifier);
			this.stepFunctionManager.acknowledgeStepFailure(
					"step-processing-error", getActivityTaskResultResponse.getTaskToken());
		}
	}
}
