/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.sfn;

import com.amazonaws.services.stepfunctions.model.GetActivityTaskResult;
import com.barraiser.onboarding.scheduling.cancellation.*;
import com.barraiser.onboarding.scheduling.cancellation.sfn.DTO.InterviewCancellationStepFunctionDTO;
import com.barraiser.onboarding.sfn.StepFunctionManager;
import com.barraiser.onboarding.sfn.StepFunctionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * This class has all the workers that process activities related to interview
 * cancellation for a
 * scheduled interview.
 */
@Log4j2
@Component
@AllArgsConstructor
public class InterviewCancellationActivityManager {
	public static final String CANCEL_BOOKED_MEETING_ACTIVITY_NAME = "cancel-booked-meeting";
	public static final String COMMUNICATE_CANCELLATION_TO_EXPERT_ACTIVITY_NAME = "communicate-cancellation-to-expert";
	public static final String COMMUNICATE_CANCELLATION_TO_CANDIDATE_ACTIVITY_NAME = "communicate-cancellation-to-candidate";
	public static final String UPDATE_INTERVIEW_FOR_CANCELLATION_ON_JIRA_ACTIVITY_NAME = "update-interview-on-jira-for-cancellation";
	public static final String CANCEL_CALENDAR_INVITES_ACTIVITY_NAME = "cancel-calendar-invites";
	public static final String STOP_INTERVIEW_CONFIRMATION_LIFECYCLE_ACTIVITY_NAME = "stop-interview-confirmation-lifecycle";
	public static final String CREATE_INTERVIEW_IN_JIRA_ACTIVITY_NAME = "create-interview-on-jira";
	public static final String SEND_UPDATED_CALENDAR_INVITE_TO_EXPERT = "send-updated-calendar-invite-to-expert";
	public static final String STOP_INTERVIEW_SCHEDULING_LIFECYCLE_ACTIVITY_NAME = "stop-interview-scheduling-lifecycle";
	public static final String DELETE_CANDIDATE_AVAILABILITY_ACTIVITY_NAME = "delete-candidate-availability";
	public static final String DELETE_DOCUMENT_FROM_FIRESTORE_ACTIVITY_NAME = "delete-document-from-firestore";
	public static final String STOP_ALL_INTERVIEW_LIFECYCLES_ACTIVITY_NAME = "stop-all-interview-lifecycles";
	public static final String CANCEL_CALENDAR_INVITE_OF_CANDIDATE_ACTIVITY_NAME = "cancel-calendar-invite-of-candidate";
	public static final String SEND_INTERVIEW_CANCELLED_EVENT_ACTIVITY_NAME = "send-interview-cancelled-event";
	public static final String COMMUNICATE_CANCELLATION_TO_PARTNER_ACTIVITY_NAME = "communicate-cancellation-to-partner";

	private final StepFunctionUtil stepFunctionUtil;
	private final StepFunctionManager stepFunctionManager;

	private final CancelMeetingProcessor cancelMeetingProcessor;
	private final InterviewUpdationOnJiraForCancellationProcessor interviewUpdationOnJiraForCancellationProcessor;
	private final InterviewCreationInJiraProcessor interviewCreationInJiraProcessor;
	private final StopInterviewConfirmationLifecycleProcessor stopInterviewConfirmationLifecycleProcessor;
	private final CancellationCommunicationToCandidateProcessor cancellationCommunicationToCandidateProcessor;
	private final CommunicationToExpertProcessor communicationToExpertProcessor;
	private final SendUpdatedCalendarInviteToExpertProcessor sendUpdatedCalendarInviteToExpertProcessor;
	private final StopInterviewSchedulingLifecycleProcessor stopInterviewSchedulingLifecycleProcessor;
	private final DeleteCandidateAvailabilityProcessor deleteCandidateAvailabilityProcessor;
	private final DeleteDocumentFromFirestoreProcessor deleteDocumentFromFirestoreProcessor;
	private final StopAllLifecyclesOfInterviewProcessor stopAllLifecyclesOfInterviewProcessor;
	private final CancelCalendarInviteOfCandidateProcessor cancelCalendarInviteOfCandidateProcessor;
	private final CancelCalendarInviteProcessor cancelCalendarInviteProcessor;
	private final ObjectMapper objectMapper;
	private final SendInterviewCancelledEventProcessor sendInterviewCancelledEventProcessor;
	private final CommunicationToPartnerProcessor communicationToPartnerProcessor;

	public void handle(final String activityName, final CancellationProcessor cancellationProcessor)
			throws Exception {
		final GetActivityTaskResult getActivityTaskResultResponse = this.stepFunctionManager
				.getGetActivityTaskResult(activityName);
		if (getActivityTaskResultResponse == null
				|| getActivityTaskResultResponse.getTaskToken() == null) {
			return;
		}

		log.info("Received task for activity " + activityName);
		InterviewCancellationStepFunctionDTO stepData = null;
		try {
			stepData = this.stepFunctionManager.getStepInput(
					getActivityTaskResultResponse,
					InterviewCancellationStepFunctionDTO.class);
			cancellationProcessor.process(stepData.getData());

			final String stepResponse = this.objectMapper.writeValueAsString(stepData);
			this.stepFunctionManager.acknowledgeStepSuccess(
					getActivityTaskResultResponse.getTaskToken(),
					stepResponse,
					"InterviewId : " + stepData.getData().getInterviewId());

		} catch (final Exception e) {
			log.info(
					"The was an exception while processing activity {} for interview {} , {} ",
					activityName,
					stepData.getData().getInterviewId(),
					e,
					e);
			this.stepFunctionUtil.sendStepFailureSlackMessage(
					stepData == null ? "" : stepData.getData().getInterviewId(),
					activityName,
					e,
					StepFunctionUtil.CANCELLATION_FLOW_FAILURE_EMAIL_NOTIFICATION_PREFIX);
			this.stepFunctionUtil.sendStepFailureEmail(
					stepData == null ? "" : stepData.getData().getInterviewId(),
					activityName,
					e,
					StepFunctionUtil.CANCELLATION_FLOW_FAILURE_EMAIL_NOTIFICATION_PREFIX);
			this.stepFunctionManager.acknowledgeStepFailure(
					"step-processing-error", getActivityTaskResultResponse.getTaskToken());
		}
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void cancelAllCalendarInvites() throws Exception {
		this.handle(CANCEL_CALENDAR_INVITES_ACTIVITY_NAME, this.cancelCalendarInviteProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void cancelCalendarInviteOfCandidateActivityName() throws Exception {
		this.handle(
				CANCEL_CALENDAR_INVITE_OF_CANDIDATE_ACTIVITY_NAME,
				this.cancelCalendarInviteOfCandidateProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void communicateCancellationToPartner() throws Exception {
		this.handle(
				COMMUNICATE_CANCELLATION_TO_PARTNER_ACTIVITY_NAME,
				this.communicationToPartnerProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void stopInterviewConfirmationLifecycle() throws Exception {
		this.handle(
				STOP_INTERVIEW_CONFIRMATION_LIFECYCLE_ACTIVITY_NAME,
				this.stopInterviewConfirmationLifecycleProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void stopInterviewSchedulingLifecycle() throws Exception {
		this.handle(
				STOP_INTERVIEW_SCHEDULING_LIFECYCLE_ACTIVITY_NAME,
				this.stopInterviewSchedulingLifecycleProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void cancelBookedMeeting() throws Exception {
		this.handle(CANCEL_BOOKED_MEETING_ACTIVITY_NAME, this.cancelMeetingProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void deleteCandidateAvailability() throws Exception {
		this.handle(
				DELETE_CANDIDATE_AVAILABILITY_ACTIVITY_NAME,
				this.deleteCandidateAvailabilityProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void deleteDocumentFromFirestore() throws Exception {
		this.handle(
				DELETE_DOCUMENT_FROM_FIRESTORE_ACTIVITY_NAME,
				this.deleteDocumentFromFirestoreProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void communicateCancellationToExpert() throws Exception {
		this.handle(
				COMMUNICATE_CANCELLATION_TO_EXPERT_ACTIVITY_NAME,
				this.communicationToExpertProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void sendCalendarInviteToExpertDuringRescheduling() throws Exception {
		this.handle(
				SEND_UPDATED_CALENDAR_INVITE_TO_EXPERT,
				this.sendUpdatedCalendarInviteToExpertProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void communicateCancellationToCandidate() throws Exception {
		this.handle(
				COMMUNICATE_CANCELLATION_TO_CANDIDATE_ACTIVITY_NAME,
				this.cancellationCommunicationToCandidateProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void createInterviewInJira() throws Exception {
		this.handle(CREATE_INTERVIEW_IN_JIRA_ACTIVITY_NAME, this.interviewCreationInJiraProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void updateInterviewForCancellationOnJira() throws Exception {
		this.handle(
				UPDATE_INTERVIEW_FOR_CANCELLATION_ON_JIRA_ACTIVITY_NAME,
				this.interviewUpdationOnJiraForCancellationProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void stopAllLifecyclesOfInterview() throws Exception {
		this.handle(
				STOP_ALL_INTERVIEW_LIFECYCLES_ACTIVITY_NAME,
				this.stopAllLifecyclesOfInterviewProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void sendInterviewCancelledEvent() throws Exception {
		this.handle(
				SEND_INTERVIEW_CANCELLED_EVENT_ACTIVITY_NAME,
				this.sendInterviewCancelledEventProcessor);
	}
}
