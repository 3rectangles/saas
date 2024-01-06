/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.lifecycle;

import com.amazonaws.services.stepfunctions.AWSStepFunctions;
import com.amazonaws.services.stepfunctions.model.StartExecutionRequest;
import com.amazonaws.services.stepfunctions.model.StartExecutionResult;
import com.amazonaws.services.stepfunctions.model.StopExecutionRequest;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.sfn.FlowType;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
@Log4j2
public class InterviewLifecycleManagementService {
	public static final String STATE_MACHINE_ARN = "arn:aws:states:ap-south-1:969111487786:stateMachine:%s-interview-lifecycle";
	private final AWSStepFunctions awsStepFunctions;
	private final Environment environment;
	private final InterviewToStepFunctionExecutionRepository interviewToStepFunctionExecutionRepository;
	private final InterViewRepository interViewRepository;

	public void startInterviewLifecycleExecution(final String interviewId, final String payload) {
		final StartExecutionResult executionResult = this.awsStepFunctions.startExecution(new StartExecutionRequest()
				.withStateMachineArn(String.format(InterviewLifecycleManagementService.STATE_MACHINE_ARN,
						this.environment.getActiveProfiles()[0]))
				.withInput(payload));
		final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId).get();

		this.interviewToStepFunctionExecutionRepository.save(InterviewToStepFunctionExecutionDAO.builder()
				.id(UUID.randomUUID().toString())
				.interviewId(interviewId)
				.ExecutionArn(executionResult.getExecutionArn())
				.flowType(FlowType.INTERVIEW_CONFIRMATION)
				.rescheduleCount(interviewDAO.getRescheduleCount())
				.build());

		InterviewLifecycleManagementService.log.info("The arn of the step function execution is {}",
				executionResult.getExecutionArn());
	}

	public void stopInterviewLifecycleExecution(final String interviewId) {
		final Optional<InterviewToStepFunctionExecutionDAO> interviewToStepFunctionExecutionDAOOptional = this.interviewToStepFunctionExecutionRepository
				.findTopByInterviewIdAndFlowTypeOrderByCreatedOnDesc(interviewId, FlowType.INTERVIEW_CONFIRMATION);

		// TBD:Felt soft delete is overkill
		if (interviewToStepFunctionExecutionDAOOptional.isPresent()) {
			final String executionArn = interviewToStepFunctionExecutionDAOOptional.get().getExecutionArn();
			this.awsStepFunctions.stopExecution(new StopExecutionRequest().withExecutionArn(executionArn));
		}

	}
}
