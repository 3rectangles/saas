/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.expert;

import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.scheduling.lifecycle.InterviewToStepFunctionExecutionDAO;
import com.barraiser.onboarding.scheduling.lifecycle.InterviewToStepFunctionExecutionRepository;
import com.barraiser.onboarding.sfn.FlowType;
import com.barraiser.onboarding.sfn.StepFunctionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Log4j2
@Component
@AllArgsConstructor
public class ExpertReminderManagementService {

	public static final String STATE_MACHINE_ARN = "arn:aws:states:ap-south-1:969111487786:stateMachine:%s-expert-reminder";
	public static String STEP_FUNCTION_NAME = "expert-reminder";

	@Qualifier("applicationEnvironment")
	private final String applicationEnvironment;
	private final StepFunctionManager stepFunctionManager;
	private final ObjectMapper objectMapper;
	private final StaticAppConfigValues staticAppConfigValues;
	private final InterviewToStepFunctionExecutionRepository interviewToStepFunctionExecutionRepository;

	private String triggerStepFunction(final ExpertReminderData data) throws Exception {
		final ExpertReminderStepFunctionDTO interviewSchedulingStepFunctionDTO = ExpertReminderStepFunctionDTO
				.builder().data(data).build();
		final String stepFunctionArn = String.format(STATE_MACHINE_ARN, this.applicationEnvironment,
				STEP_FUNCTION_NAME);
		final String executionArn = this.stepFunctionManager.startExecution(stepFunctionArn, this.objectMapper
				.writeValueAsString(interviewSchedulingStepFunctionDTO), UUID.randomUUID().toString());
		log.info("Triggered {} step function for interview {}", stepFunctionArn,
				data.getInterviewId());
		return executionArn;
	}

	private void saveExecutionInformation(final InterviewDAO interviewDAO,
			final String executionArn) {

		this.interviewToStepFunctionExecutionRepository.save(
				InterviewToStepFunctionExecutionDAO.builder()
						.id(UUID.randomUUID().toString())
						.interviewId(interviewDAO.getId())
						.flowType(FlowType.EXPERT_REMINDER)
						.ExecutionArn(executionArn)
						.rescheduleCount(interviewDAO.getRescheduleCount())
						.build());
	}

	public void startExpertReminder(final InterviewDAO interviewDAO) throws Exception {
		final Boolean isStepFunctionEnabled = Boolean
				.parseBoolean(this.staticAppConfigValues.getInterviewLifecycleManagementEnabled());
		if (isStepFunctionEnabled) {
			final ExpertReminderData expertReminderData = new ExpertReminderData();
			expertReminderData.setInterviewId(interviewDAO.getId());
			expertReminderData.setInterviewDAO(interviewDAO);
			final String executionArn = this.triggerStepFunction(expertReminderData);
			this.saveExecutionInformation(interviewDAO, executionArn);
		}
	}
}
