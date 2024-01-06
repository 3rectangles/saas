/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling;

import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.PartnerConfigManager;
import com.barraiser.onboarding.scheduling.dto.InterviewSchedulingStepFunctionDTO;
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
public class InterviewSchedulingStepFunctionProcessor implements SchedulingProcessor {
	public static final String STATE_MACHINE_ARN = "arn:aws:states:ap-south-1:969111487786:stateMachine:%s-interview-scheduling";
	public static String STEP_FUNCTION_NAME = "interview-scheduling";

	@Qualifier("applicationEnvironment")
	private final String applicationEnvironment;
	private final StepFunctionManager stepFunctionManager;
	private final ObjectMapper objectMapper;
	private final StaticAppConfigValues staticAppConfigValues;
	private final InterviewToStepFunctionExecutionRepository interviewToStepFunctionExecutionRepository;
	private final InterViewRepository interViewRepository;
	private final PartnerConfigManager partnerConfigManager;

	@Override
	public void process(SchedulingProcessingData data) throws Exception {
		final Boolean isStepFunctionEnabled = Boolean
				.parseBoolean(this.staticAppConfigValues.getInterviewLifecycleManagementEnabled());
		if (isStepFunctionEnabled) {
			data.setPartnerId(this.partnerConfigManager
					.getPartnerCompanyForInterviewId(data.getInput().getInterviewId()).getId());
			final String executionArn = this.triggerStepFunction(data);
			this.saveExecutionInformation(data, executionArn);
		}
	}

	private String triggerStepFunction(final SchedulingProcessingData schedulingProcessingData) throws Exception {
		final InterviewSchedulingStepFunctionDTO interviewSchedulingStepFunctionDTO = InterviewSchedulingStepFunctionDTO
				.builder().data(schedulingProcessingData).build();
		final String stepFunctionArn = String.format(STATE_MACHINE_ARN, this.applicationEnvironment,
				STEP_FUNCTION_NAME);
		final String executionArn = this.stepFunctionManager.startExecution(stepFunctionArn, this.objectMapper
				.writeValueAsString(interviewSchedulingStepFunctionDTO), UUID.randomUUID().toString());
		log.info("Triggered {} step function for interview {}", stepFunctionArn,
				schedulingProcessingData.getInput().getInterviewId());
		return executionArn;
	}

	private void saveExecutionInformation(final SchedulingProcessingData schedulingProcessingData,
			final String executionArn) {
		final InterviewDAO interviewDAO = this.interViewRepository
				.findById(schedulingProcessingData.getInput().getInterviewId()).get();

		this.interviewToStepFunctionExecutionRepository.save(
				InterviewToStepFunctionExecutionDAO.builder()
						.id(UUID.randomUUID().toString())
						.interviewId(schedulingProcessingData.getInput().getInterviewId())
						.flowType(FlowType.INTERVIEW_SCHEDULING)
						.ExecutionArn(executionArn)
						.rescheduleCount(interviewDAO.getRescheduleCount())
						.build());
	}
}
