/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.confirmation.sfn;

import com.amazonaws.services.stepfunctions.model.GetActivityTaskResult;
import com.barraiser.onboarding.scheduling.confirmation.*;
import com.barraiser.onboarding.scheduling.confirmation.dto.InterviewConfirmationLifecycleDTO;
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
public class InterviewConfirmationTasksProcessorV2 {
	private final ObjectMapper objectMapper;

	private final StepFunctionManager stepFunctionManager;
	private final StepFunctionUtil stepFunctionUtil;
	private final GetInterviewInformationProcessor getInterviewInformationProcessor;
	private final GetConfirmationStatusAndSetWaitTimeProcessor getConfirmationStatusAndSetWaitTimeProcessor;
	private final NotifyForManualConfirmationProcessor notifyForManualConfirmationProcessor;
	private final GetInterviewStatusForReminderProcessor getInterviewStatusForReminderProcessor;

	public void handle(final String activityName, final ConfirmationProcessor confirmationProcessor)
			throws Exception {
		final GetActivityTaskResult getActivityTaskResultResponse = this.stepFunctionManager
				.getGetActivityTaskResult(activityName);
		if (getActivityTaskResultResponse == null
				|| getActivityTaskResultResponse.getTaskToken() == null) {
			return;
		}

		log.info("Received task for activity " + activityName);
		InterviewConfirmationLifecycleDTO stepData = null;
		try {
			stepData = this.stepFunctionManager.getStepInput(
					getActivityTaskResultResponse,
					InterviewConfirmationLifecycleDTO.class);
			confirmationProcessor.process(stepData);

			final String stepResponse = this.objectMapper.writeValueAsString(stepData);
			this.stepFunctionManager.acknowledgeStepSuccess(
					getActivityTaskResultResponse.getTaskToken(),
					stepResponse,
					"InterviewId : " + stepData.getInterviewId());

		} catch (final Exception e) {
			log.info(
					"The was an exception while processing activity {} for interview {} , {} ",
					activityName,
					stepData.getInterviewId(),
					e,
					e);
			this.stepFunctionUtil.sendStepFailureSlackMessage(
					stepData == null ? "" : stepData.getInterviewId(),
					activityName,
					e,
					StepFunctionUtil.CONFIRMATION_FLOW_FAILURE_EMAIL_NOTIFICATION_PREFIX);
			this.stepFunctionUtil.sendStepFailureEmail(
					stepData == null ? "" : stepData.getInterviewId(),
					activityName,
					e,
					StepFunctionUtil.CONFIRMATION_FLOW_FAILURE_EMAIL_NOTIFICATION_PREFIX);
			this.stepFunctionManager.acknowledgeStepFailure(
					"step-processing-error", getActivityTaskResultResponse.getTaskToken());
		}
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void getInterviewInformation() throws Exception {
		this.handle(ConfirmationConstants.GET_INTERVIEW_INFORMATION, this.getInterviewInformationProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void getInterviewConfirmationStatusAndSetWaitTimInterviewConfirmationTasksProcessoreForNextTurn()
			throws Exception {
		this.handle(ConfirmationConstants.GET_INTERVIEW_CONFIRMATION_STATUS,
				this.getConfirmationStatusAndSetWaitTimeProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void callCandidateForConfirmationManually() throws Exception {
		this.handle(ConfirmationConstants.CALL_CANDIDATE_FOR_CONFIRMATION_MANUALLY,
				this.notifyForManualConfirmationProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void getInterviewStatusForReminderProcessor() throws Exception {
		this.handle(ConfirmationConstants.GET_INTERVIEW_STATUS_FOR_REMINDER,
				this.getInterviewStatusForReminderProcessor);
	}
}
