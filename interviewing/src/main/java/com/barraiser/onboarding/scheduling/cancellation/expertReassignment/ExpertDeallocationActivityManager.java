/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment;

import com.amazonaws.services.stepfunctions.model.GetActivityTaskResult;
import com.barraiser.onboarding.scheduling.cancellation.SearchNewInterviewForExpertProcessor;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertDeallocationStepFunctionDTO;
import com.barraiser.onboarding.sfn.StepFunctionManager;
import com.barraiser.onboarding.sfn.StepFunctionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
@Deprecated(forRemoval = true)
public class ExpertDeallocationActivityManager {
	public static final String SEARCH_NEW_INTERVIEW_FOR_EXPERT_ACTIVITY_NAME = "search-new-interview-for-expert";
	public static final String ASSIGN_NEW_INTERVIEW_TO_EXPERT_ACTIVITY_NAME = "assign-new-interview-to-expert";

	private final StepFunctionManager stepFunctionManager;
	private final ObjectMapper objectMapper;
	private final StepFunctionUtil stepFunctionUtil;
	private final SearchNewInterviewForExpertProcessor searchNewInterviewForExpertProcessor;
	private final AssignNewInterviewToOriginalExpertProcessor assignNewInterviewToOriginalExpertProcessor;

	private void handle(final String activityName, final ExpertDeAllocationProcessor expertDeAllocationProcessor)
			throws Exception {
		final GetActivityTaskResult getActivityTaskResultResponse = this.stepFunctionManager
				.getGetActivityTaskResult(activityName);
		if (getActivityTaskResultResponse == null || getActivityTaskResultResponse.getTaskToken() == null) {
			return;
		}
		log.info("Received task for activity " + activityName);
		ExpertDeallocationStepFunctionDTO stepData = null;
		try {
			stepData = this.stepFunctionManager.getStepInput(getActivityTaskResultResponse,
					ExpertDeallocationStepFunctionDTO.class);
			expertDeAllocationProcessor.process(stepData.getData());
			final String stepResponse = this.objectMapper.writeValueAsString(stepData);
			this.stepFunctionManager.acknowledgeStepSuccess(getActivityTaskResultResponse.getTaskToken(), stepResponse,
					"InterviewId : " + stepData.getData().getInterviewId());
		} catch (final Exception e) {
			log.info("The was an exception while processing activity {} for interview {} , {} ", activityName,
					stepData.getData().getInterviewId(), e, e);
			this.stepFunctionUtil.sendStepFailureSlackMessage(
					stepData == null ? "" : stepData.getData().getInterviewId(),
					activityName, e, StepFunctionUtil.EXPERT_DEALLOCATION_FLOW_FAILURE_EMAIL_NOTIFICATION_PREFIX);
			this.stepFunctionUtil.sendStepFailureEmail(stepData == null ? "" : stepData.getData().getInterviewId(),
					activityName, e, StepFunctionUtil.EXPERT_DEALLOCATION_FLOW_FAILURE_EMAIL_NOTIFICATION_PREFIX);
			this.stepFunctionManager.acknowledgeStepFailure("step-processing-error",
					getActivityTaskResultResponse.getTaskToken());
		}
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void searchForNewInterviewForExpert() throws Exception {
		this.handle(SEARCH_NEW_INTERVIEW_FOR_EXPERT_ACTIVITY_NAME, this.searchNewInterviewForExpertProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void assignNewInterviewToExpert() throws Exception {
		this.handle(ASSIGN_NEW_INTERVIEW_TO_EXPERT_ACTIVITY_NAME, this.assignNewInterviewToOriginalExpertProcessor);
	}
}
