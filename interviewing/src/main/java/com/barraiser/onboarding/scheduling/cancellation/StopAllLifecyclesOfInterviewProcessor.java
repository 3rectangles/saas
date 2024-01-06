/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.scheduling.lifecycle.InterviewToStepFunctionExecutionDAO;
import com.barraiser.onboarding.scheduling.lifecycle.InterviewToStepFunctionExecutionRepository;
import com.barraiser.onboarding.sfn.FlowType;
import com.barraiser.onboarding.sfn.StepFunctionManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@AllArgsConstructor
@Component
public class StopAllLifecyclesOfInterviewProcessor implements CancellationProcessor {
	public static final List<FlowType> FLOWS_TO_STOP = List.of(FlowType.INTERVIEW_CONFIRMATION,
			FlowType.INTERVIEW_SCHEDULING, FlowType.EXPERT_REASSIGNMENT, FlowType.EXPERT_ALLOCATION,
			FlowType.TA_ALLOCATION, FlowType.INTERVIEWING_LIFECYCLE, FlowType.EXPERT_REMINDER);

	private final InterviewToStepFunctionExecutionRepository interviewToStepFunctionExecutionRepository;
	private final StepFunctionManager stepFunctionManager;

	@Override
	public void process(final CancellationProcessingData data) throws Exception {
		final String interviewId = data.getPreviousStateOfCancelledInterview().getId();
		final Integer rescheduleCount = data.getPreviousStateOfCancelledInterview().getRescheduleCount();
		final List<InterviewToStepFunctionExecutionDAO> interviewLifecycles = this.getAllLifecyclesOfInterview(
				interviewId,
				rescheduleCount, FLOWS_TO_STOP);
		this.stopAllLifecyclesOfInterview(interviewLifecycles);
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

	private List<InterviewToStepFunctionExecutionDAO> getAllLifecyclesOfInterview(
			final String interviewId,
			final Integer rescheduleCount,
			final List<FlowType> flowTypes) {
		return this.interviewToStepFunctionExecutionRepository
				.findAllByInterviewIdAndRescheduleCountAndFlowTypeIn(
						interviewId, rescheduleCount, flowTypes);
	}
}
