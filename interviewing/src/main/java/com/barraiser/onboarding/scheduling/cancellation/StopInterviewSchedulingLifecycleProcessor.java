/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.scheduling.lifecycle.InterviewToStepFunctionExecutionDAO;
import com.barraiser.onboarding.scheduling.lifecycle.InterviewToStepFunctionExecutionRepository;
import com.barraiser.onboarding.sfn.FlowType;
import com.barraiser.onboarding.sfn.StepFunctionManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Log4j2
@AllArgsConstructor
@Component
public class StopInterviewSchedulingLifecycleProcessor implements CancellationProcessor {
	private final InterviewToStepFunctionExecutionRepository interviewToStepFunctionExecutionRepository;
	private final StepFunctionManager stepFunctionManager;

	@Override
	public void process(final CancellationProcessingData data) throws Exception {

		final Optional<InterviewToStepFunctionExecutionDAO> interviewToStepFunctionExecutionDAOOptional = this.interviewToStepFunctionExecutionRepository
				.findTopByInterviewIdAndFlowTypeAndRescheduleCountOrderByCreatedOnDesc(
						data.getInterviewId(), FlowType.INTERVIEW_SCHEDULING,
						data.getPreviousStateOfCancelledInterview().getRescheduleCount());
		if (interviewToStepFunctionExecutionDAOOptional.isPresent() &&
				this.stepFunctionManager
						.isExecutionRunning(interviewToStepFunctionExecutionDAOOptional.get().getExecutionArn())) {
			this.stepFunctionManager.stopExecution(interviewToStepFunctionExecutionDAOOptional.get().getExecutionArn());
		}
	}
}
