/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.sfn;

import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.model.*;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Log4j2
public class StepFunctionManager {

	public static final String ACTIVITY_ARN_PREFIX = "arn:aws:states:ap-south-1:969111487786:activity:";

	private final AWSStepFunctions awsStepFunctions;
	private final Environment environment;
	private final StaticAppConfigValues staticAppConfigValues;
	private final ObjectMapper objectMapper;

	/**
	 * Triggers execution of the step function flow
	 *
	 * @param stateMachineArn
	 *            AWS resource ARN for the statemachine
	 * @param payload
	 *            input to the step function
	 * @param executionName
	 *            This can be used to ensure idempotency while triggering the flow.
	 *            A state machine triggered with same execution name and input will
	 *            return
	 *            same arn. (basically not trigger new flow. Useful in race
	 *            conditions to
	 *            prevent duplicate flow from getting triggered)
	 * @return
	 */
	public String startExecution(final String stateMachineArn, final String payload, final String executionName) {

		final StartExecutionResult executionResult = this.awsStepFunctions.startExecution(new StartExecutionRequest()
				.withStateMachineArn(String.format(stateMachineArn, this.environment.getActiveProfiles()[0]))
				.withName(executionName)
				.withInput(payload));

		log.info("The arn of the step function execution is {}", executionResult.getExecutionArn());

		return executionResult.getExecutionArn();
	}

	/**
	 * Kills the execution for the given
	 * execution ARN.
	 *
	 * @param executionArn
	 */
	public void stopExecution(final String executionArn) {
		this.awsStepFunctions.stopExecution(new StopExecutionRequest().withExecutionArn(executionArn));
		log.info("Step function execution with arn {} has been killed", executionArn);
	}

	/**
	 * Retrieves step input and maps
	 * returns the object.
	 *
	 * @param getActivityTaskResult
	 *            AWS defined Class that holds the input and the task token
	 * @param clazz
	 *            Class to which we want the input object to be mapped.
	 * @param <T>
	 * @return
	 * @throws Exception
	 */
	public <T> T getStepInput(final GetActivityTaskResult getActivityTaskResult, Class<T> clazz) throws Exception {
		final T stepInput;

		try {
			stepInput = this.objectMapper.readValue(getActivityTaskResult.getInput(), clazz);
		} catch (final JsonProcessingException exception) {
			throw new Exception("Error getting step input: " + getActivityTaskResult.getInput());
		}

		return stepInput;
	}

	/**
	 * @param activityName
	 * @return
	 */
	public GetActivityTaskResult getGetActivityTaskResult(final String activityName) {
		if (Boolean.parseBoolean(this.staticAppConfigValues.getInterviewLifecycleManagementEnabled())) {
			final String activityArn = String.format("%s%s-%s", ACTIVITY_ARN_PREFIX,
					this.environment.getActiveProfiles()[0], activityName);
			// log.info("Polling {}", activityArn);

			return this.awsStepFunctions.getActivityTask(new GetActivityTaskRequest()
					.withActivityArn(activityArn)
					.withWorkerName(activityName)
					.withSdkRequestTimeout(65000));
		}
		return null;
	}

	/**
	 * @param taskToken
	 * @param response
	 * @param flowIdentifier
	 * @throws Exception
	 */
	public void acknowledgeStepSuccess(final String taskToken, final String response, final String flowIdentifier)
			throws Exception {
		try {
			log.info("Response of activity : {}", response);
			this.awsStepFunctions.sendTaskSuccess(new SendTaskSuccessRequest()
					.withTaskToken(taskToken)
					.withOutput(response));
		} catch (final TaskTimedOutException exception) {
			throw new Exception("Acknowledge Step Function task timed out: " + flowIdentifier);
		}
	}

	/**
	 * @param taskToken
	 * @throws Exception
	 */
	public void acknowledgeStepFailure(final String error, final String taskToken) throws Exception {
		try {
			this.awsStepFunctions.sendTaskFailure(new SendTaskFailureRequest()
					.withError(error)
					.withTaskToken(taskToken));
		} catch (final TaskTimedOutException exception) {
			throw new Exception("Acknowledge Step Function Failure task timed out");
		}
	}

	/**
	 * @param executionArn
	 * @throws Exception
	 */
	public DescribeExecutionResult getExecutionInformation(final String executionArn) throws Exception {
		try {
			return this.awsStepFunctions
					.describeExecution(new DescribeExecutionRequest().withExecutionArn(executionArn));
		} catch (final TaskTimedOutException exception) {
			throw new Exception("Exception while getting step function execution status");
		}
	}

	public Boolean isExecutionRunning(final String executionArn) throws Exception {
		final String executionStatus = this.getExecutionInformation(executionArn).getStatus();
		return ExecutionStatus.RUNNING.toString().equalsIgnoreCase(executionStatus);
	}

}
