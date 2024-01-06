/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.ta;

import com.amazonaws.services.stepfunctions.model.GetActivityTaskResult;

import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessor;
import com.barraiser.onboarding.scheduling.scheduling.SendTaCalendarInviteProcessor;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.AllocateTaProcessor;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.AllocateTaWithoutOverBookingProcessor;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.SchedulingCommunicationToTaProcessor;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.TaJiraUpdateProcessor;
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
public class TaAllocationTasksProcessor {

	private final ObjectMapper objectMapper;

	private final StepFunctionUtil stepFunctionUtil;
	private final StepFunctionManager stepFunctionManager;
	public static final String SEND_TA_ALLOCATION_MAIL = "send-ta-allocation-mail";
	public static final String ALLOCATE_TA = "allocate-ta";
	public static final String UPDATE_TA_IN_JIRA = "update-ta-in-jira";
	public static final String COMMENT_IN_JIRA_IF_NOT_ASSIGNED = "comment-in-jira-if-ta-not-assigned";
	public static final String SEND_INTERVIEW_SCHEDULED_CALENDAR_INVITE_TO_TA = "send-interview-scheduled-calendar-invite-to-ta";
	public static final String ALLOCATE_TA_WITHOUT_OVERBOOKING = "allocate-ta-without-overbooking";

	private final AllocateTaProcessor allocateTaProcessor;
	private final TaJiraProcessor taJiraCommentUpdator;

	private final SchedulingCommunicationToTaProcessor schedulingCommunicationToTaProcessor;
	private final TaJiraUpdateProcessor taJiraFieldUpdator;
	private final SendTaCalendarInviteProcessor sendTaCalendarInviteProcessor;
	private final AllocateTaWithoutOverBookingProcessor allocateTaWithoutOverBookingProcessor;

	@Scheduled(fixedDelayString = "${scheduled.fixedDelayTaAllocation}")
	public void allocateTa() throws Exception {
		this.handle(ALLOCATE_TA, this.allocateTaProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelayTaAllocation}")
	public void sendTaAllocationMail() throws Exception {
		this.handle(SEND_TA_ALLOCATION_MAIL, this.schedulingCommunicationToTaProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelayTaAllocation}")
	public void updateTaInJira() throws Exception {
		this.handle(UPDATE_TA_IN_JIRA, this.taJiraFieldUpdator);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelayTaAllocation}")
	public void commentInJiraIfTaNotAssigned() throws Exception {
		this.handle(COMMENT_IN_JIRA_IF_NOT_ASSIGNED, this.taJiraCommentUpdator);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelayTaAllocation}")
	public void sendInterviewScheduledCalendarInviteToTa() throws Exception {
		this.handle(SEND_INTERVIEW_SCHEDULED_CALENDAR_INVITE_TO_TA, this.sendTaCalendarInviteProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelayTaAllocation}")
	public void allocateTaWithoutOverBooking() throws Exception {
		this.handle(ALLOCATE_TA_WITHOUT_OVERBOOKING, this.allocateTaWithoutOverBookingProcessor);
	}

	public void handle(final String activityName, final SchedulingProcessor schedulingProcessor) throws Exception {
		final GetActivityTaskResult getActivityTaskResultResponse = this.stepFunctionManager
				.getGetActivityTaskResult(activityName);
		if (getActivityTaskResultResponse == null || getActivityTaskResultResponse.getTaskToken() == null) {
			return;
		}

		log.info("Received task for activity " + activityName);
		SchedulingProcessingData stepData = null;
		try {
			stepData = this.stepFunctionManager.getStepInput(getActivityTaskResultResponse,
					SchedulingProcessingData.class);
			schedulingProcessor.process(stepData);

			final String stepResponse = this.objectMapper.writeValueAsString(stepData);
			this.stepFunctionManager.acknowledgeStepSuccess(getActivityTaskResultResponse.getTaskToken(), stepResponse,
					"InterviewId : " + stepData.getInput().getInterviewId());

		} catch (Exception e) {
			log.info("The was an exception while processing activity %s for int" + activityName, e, e);
			this.stepFunctionUtil.sendStepFailureSlackMessage(
					stepData == null ? "" : stepData.getInput().getInterviewId(), activityName, e,
					StepFunctionUtil.TA_SCHEDULING_FLOW_FAILURE_EMAIL_NOTIFICATION_PREFIX);
			this.stepFunctionUtil.sendStepFailureEmail(stepData == null ? "" : stepData.getInput().getInterviewId(),
					activityName, e, StepFunctionUtil.TA_SCHEDULING_FLOW_FAILURE_EMAIL_NOTIFICATION_PREFIX);
			this.stepFunctionManager.acknowledgeStepFailure("step-processing-error",
					getActivityTaskResultResponse.getTaskToken());
		}
	}

}
