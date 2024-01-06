/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.expert_deallocation;

import com.barraiser.onboarding.scheduling.expert_deallocation.dto.ExpertDeAllocatorData;
import com.barraiser.onboarding.scheduling.lifecycle.InterviewToStepFunctionExecutionDAO;
import com.barraiser.onboarding.scheduling.lifecycle.InterviewToStepFunctionExecutionRepository;
import com.barraiser.onboarding.sfn.FlowType;
import com.barraiser.onboarding.sfn.StepFunctionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
public class StopAllExpertLifecyclesActivity implements ExpertDeallocationSfnActivity {
	public static final List<FlowType> FLOWS_TO_STOP = List.of(FlowType.EXPERT_REMINDER);

	private final ObjectMapper objectMapper;
	private final InterviewToStepFunctionExecutionRepository interviewToStepFunctionExecutionRepository;
	private final StepFunctionManager stepFunctionManager;

	@Override
	public String name() {
		return "stop-all-expert-lifecycles";
	}

	@Override
	public ExpertDeAllocatorData process(final String input) throws Exception {
		final ExpertDeAllocatorData data = this.objectMapper.readValue(input, ExpertDeAllocatorData.class);
		final List<InterviewToStepFunctionExecutionDAO> interviewLifecycles = this.getAllLifecyclesOfInterview(
				data.getInterviewId(),
				data.getInterview().getRescheduleCount(), FLOWS_TO_STOP);
		this.stopAllLifecyclesOfInterview(interviewLifecycles);
		return data;
	}

	private List<InterviewToStepFunctionExecutionDAO> getAllLifecyclesOfInterview(
			final String interviewId,
			final Integer rescheduleCount,
			final List<FlowType> flowTypes) {
		return this.interviewToStepFunctionExecutionRepository
				.findAllByInterviewIdAndRescheduleCountAndFlowTypeIn(
						interviewId, rescheduleCount, flowTypes);
	}

	private void stopAllLifecyclesOfInterview(
			final List<InterviewToStepFunctionExecutionDAO> interviewLifecycles) throws Exception {
		for (final InterviewToStepFunctionExecutionDAO interviewLifecycle : interviewLifecycles) {
			if (interviewLifecycle != null
					&& this.stepFunctionManager.isExecutionRunning(
							interviewLifecycle.getExecutionArn())) {
				this.stepFunctionManager.stopExecution(interviewLifecycle.getExecutionArn());
			}
		}
	}
}
