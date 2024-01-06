/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.sfn;

import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.model.StartExecutionRequest;
import com.amazonaws.services.stepfunctions.model.StartExecutionResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@AllArgsConstructor
@Log4j2
public class StepFunctionInitiator {
	private final AWSStepFunctions awsStepFunctions;
	private final Environment environment;
	private final ObjectMapper objectMapper;

	public String startExecution(final String stateMachineArn, final Object payload) throws JsonProcessingException {
		final Map<String, Object> data = new HashMap<>();
		data.put("data", payload);
		final StartExecutionResult executionResult = this.awsStepFunctions.startExecution(new StartExecutionRequest()
				.withStateMachineArn(String.format(stateMachineArn, this.environment.getActiveProfiles()[0]))
				.withName(UUID.randomUUID().toString())
				.withInput(this.objectMapper.writeValueAsString(data)));

		log.info("The arn of the step function execution is {}", executionResult.getExecutionArn());

		return executionResult.getExecutionArn();
	}
}
