/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment;

import com.amazonaws.services.stepfunctions.model.GetActivityTaskResult;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertReassignmentStepFunctionDTO;
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
public class ExpertReassignmentActivityManager {
	public static final String ALLOCATE_EXPERT_ACTIVITY_NAME = "allocate-expert";
	public static final String DECIDE_EXPERT_REASSIGNMENT_ACTIVITY_NAME = "decide-expert-reassignment";
	public static final String FETCH_EXPERT_FOR_REASSIGNMENT_ACTIVITY_NAME = "fetch-expert-for-reassignment";
	public static final String NOTIFY_MANUAL_EXPERT_REASSIGNMENT_ACTIVITY_NAME = "notify-manual-expert-reassignment";
	public static final String NOTIFY_EXPERT_REASSIGNMENT_FAILURE_ACTIVITY_NAME = "notify-expert-reassignment-failure";
	public static final String CANCEL_INTERVIEW_ACTIVITY_NAME = "cancel-interview";

	private final StepFunctionManager stepFunctionManager;
	private final ObjectMapper objectMapper;
	private final StepFunctionUtil stepFunctionUtil;
	private final AllocateExpertProcessor allocateExpertProcessor;
	private final FetchInterviewerForInterviewProcessor fetchInterviewerForInterviewProcessor;
	private final DecideExpertReassignmentProcessor decideExpertReassignmentProcessor;
	private final ManualExpertAssignmentNotificationProcessor manualExpertAssignmentNotificationProcessor;
	private final NotifyExpertReassignmentFailedProcessor notifyExpertReassignmentFailedProcessor;
	private final InterviewCancellationProcessor interviewCancellationProcessor;

	private void handle(final String activityName, final ExpertReassignmentProcessor expertReassignmentProcessor)
			throws Exception {
		final GetActivityTaskResult getActivityTaskResultResponse = this.stepFunctionManager
				.getGetActivityTaskResult(activityName);
		if (getActivityTaskResultResponse == null || getActivityTaskResultResponse.getTaskToken() == null) {
			return;
		}
		log.info("Received task for activity " + activityName);
		ExpertReassignmentStepFunctionDTO stepData = null;
		try {
			stepData = this.stepFunctionManager.getStepInput(getActivityTaskResultResponse,
					ExpertReassignmentStepFunctionDTO.class);
			expertReassignmentProcessor.process(stepData.getData());
			final String stepResponse = this.objectMapper.writeValueAsString(stepData);
			this.stepFunctionManager.acknowledgeStepSuccess(getActivityTaskResultResponse.getTaskToken(), stepResponse,
					"InterviewId : " + stepData.getData().getInterviewId());
		} catch (final Exception e) {
			log.info("The was an exception while processing activity {} for interview {} , {} ", activityName,
					stepData.getData().getInterviewId(), e, e);
			this.stepFunctionUtil.sendStepFailureSlackMessage(
					stepData == null ? "" : stepData.getData().getInterviewId(),
					activityName, e, StepFunctionUtil.EXPERT_REASSIGNMENT_FLOW_FAILURE_EMAIL_NOTIFICATION_PREFIX);
			this.stepFunctionUtil.sendStepFailureEmail(stepData == null ? "" : stepData.getData().getInterviewId(),
					activityName, e, StepFunctionUtil.EXPERT_REASSIGNMENT_FLOW_FAILURE_EMAIL_NOTIFICATION_PREFIX);
			this.stepFunctionManager.acknowledgeStepFailure("step-processing-error",
					getActivityTaskResultResponse.getTaskToken());
		}
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void decideExpertReassignment() throws Exception {
		this.handle(DECIDE_EXPERT_REASSIGNMENT_ACTIVITY_NAME, this.decideExpertReassignmentProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void findExpertForReassignment() throws Exception {
		this.handle(FETCH_EXPERT_FOR_REASSIGNMENT_ACTIVITY_NAME, this.fetchInterviewerForInterviewProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void allocateNewExpertToInterview() throws Exception {
		this.handle(ALLOCATE_EXPERT_ACTIVITY_NAME, this.allocateExpertProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void notifyManualExpertReassignment() throws Exception {
		this.handle(NOTIFY_MANUAL_EXPERT_REASSIGNMENT_ACTIVITY_NAME, this.manualExpertAssignmentNotificationProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void notifyExpertReassignmentFailed() throws Exception {
		this.handle(NOTIFY_EXPERT_REASSIGNMENT_FAILURE_ACTIVITY_NAME, this.notifyExpertReassignmentFailedProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void cancelInterview() throws Exception {
		this.handle(CANCEL_INTERVIEW_ACTIVITY_NAME, this.interviewCancellationProcessor);
	}
}
