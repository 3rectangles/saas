/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.scheduling.lifecycle.InterviewToStepFunctionExecutionDAO;
import com.barraiser.onboarding.scheduling.lifecycle.InterviewToStepFunctionExecutionRepository;
import com.barraiser.onboarding.scheduling.scheduling.sfn.InterviewSchedulingActivity;
import com.barraiser.onboarding.sfn.FlowType;
import com.barraiser.onboarding.sfn.StepFunctionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
@Log4j2
public class WaitForCancellationToCompleteActivity implements InterviewSchedulingActivity {
	private final InterviewToStepFunctionExecutionRepository interviewToStepFunctionExecutionRepository;
	private final InterViewRepository interViewRepository;
	private final StepFunctionManager stepFunctionManager;
	private final ObjectMapper objectMapper;
	public static final String WAIT_FOR_CANCELLATION_TO_COMPLETE = "wait-for-cancellation-to-complete";

	private String getPreviousCancellationExecutionArn(final String interviewId) {
		final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId).get();
		final Optional<InterviewToStepFunctionExecutionDAO> executionDAO = this.interviewToStepFunctionExecutionRepository
				.findTopByInterviewIdAndFlowTypeAndRescheduleCountOrderByCreatedOnDesc(interviewDAO.getId(),
						FlowType.INTERVIEW_CANCELLATION, interviewDAO.getRescheduleCount() - 1);
		return executionDAO.map(InterviewToStepFunctionExecutionDAO::getExecutionArn).orElse(null);
	}

	@Override
	public String name() {
		return WAIT_FOR_CANCELLATION_TO_COMPLETE;
	}

	@Override
	public SchedulingProcessingData process(String input) throws Exception {
		final SchedulingProcessingData data = objectMapper.readValue(input, SchedulingProcessingData.class);
		final String cancellationExecutionArn = this
				.getPreviousCancellationExecutionArn(data.getInput().getInterviewId());
		if (cancellationExecutionArn != null && this.stepFunctionManager.isExecutionRunning(cancellationExecutionArn)) {
			throw new RuntimeException(
					"cancellation is still running for interview id : " + data.getInput().getInterviewId());
		}
		return data;
	}
}
