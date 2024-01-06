/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.sfn;

import com.barraiser.onboarding.featureToggle.FeatureToggleNames;
import com.barraiser.onboarding.featureToggle.InterviewLevelFeatureToggleManager;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interviewing.step_function.dto.InterviewingLifecycleDTO;
import com.barraiser.onboarding.scheduling.lifecycle.InterviewToStepFunctionExecutionDAO;
import com.barraiser.onboarding.scheduling.lifecycle.InterviewToStepFunctionExecutionRepository;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import com.barraiser.onboarding.sfn.FlowType;
import com.barraiser.onboarding.sfn.StepFunctionInitiator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Log4j2
@AllArgsConstructor
@Component
public class TriggerInterviewingLifecycleActivity implements InterviewSchedulingActivity {
	public static final String STATE_MACHINE_ARN = "arn:aws:states:ap-south-1:969111487786:stateMachine:interviewing-lifecycle-%s";
	public static final String TRIGGER_INTERVIEWING_LIFECYCLE = "trigger-interviewing-lifecycle";

	private final Environment environment;
	private final InterviewToStepFunctionExecutionRepository interviewToStepFunctionExecutionRepository;
	private final InterViewRepository interViewRepository;
	private final StepFunctionInitiator stepFunctionInitiator;
	private final InterviewLevelFeatureToggleManager featureToggleManager;
	private final ObjectMapper objectMapper;

	@Override
	public String name() {
		return TRIGGER_INTERVIEWING_LIFECYCLE;
	}

	@Override
	public SchedulingProcessingData process(String input) throws Exception {
		final SchedulingProcessingData data = objectMapper.readValue(input, SchedulingProcessingData.class);
		final String interviewId = data.getInput().getInterviewId();

		if (!this.featureToggleManager.isFeatureOn(interviewId, FeatureToggleNames.INTERVIEW_BOT)) {
			return data;
		}

		final InterviewingLifecycleDTO interviewingLifecycleDTO = InterviewingLifecycleDTO.builder()
				.interviewId(interviewId)
				.build();

		final String executionArn = this.stepFunctionInitiator.startExecution(
				String.format(STATE_MACHINE_ARN, this.environment.getActiveProfiles()[0]), interviewingLifecycleDTO);

		this.interviewToStepFunctionExecutionRepository.save(InterviewToStepFunctionExecutionDAO.builder()
				.id(UUID.randomUUID().toString())
				.interviewId(interviewId)
				.ExecutionArn(executionArn)
				.flowType(FlowType.INTERVIEWING_LIFECYCLE)
				.rescheduleCount(this.interViewRepository.findById(interviewId).get().getRescheduleCount())
				.build());
		return data;
	}
}
