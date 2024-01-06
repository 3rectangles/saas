/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.ta;

import com.barraiser.onboarding.scheduling.cancellation.*;
import com.barraiser.onboarding.scheduling.cancellation.sfn.InterviewCancellationActivityManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class TaReassignmentActivityManager {
	public static final String ASSIGNMENT_OF_NEW_INTERVIEW_TO_TA_ACTIVITY_NAME = "assign-new-interview-to-ta";
	public static final String UPDATE_NEW_TA_DETAILS_ON_JIRA = "update-new-ta-details-on-jira";
	public static final String SEND_REASSIGNMENT_MAIL_TO_TA = "send-reassignment-mail-to-ta";
	public static final String FREE_TA_SLOT_ACTIVITY_NAME = "free-ta-slot";
	public static final String SEARCH_NEW_INTERVIEW_FOR_TA = "search-new-interview-for-ta";
	public static final String SEND_REASSIGNMENT_INVITE_TO_TA = "send-reassignment-invite-to-ta";
	public static final String CANCEL_CALENDAR_INVITE_OF_TA = "cancel-calendar-invite-of-ta";

	private final AssignNewInterviewToTaProcessor assignNewInterviewToTaProcessor;
	private final UpdateNewTaDetailsOnJiraProcessor updateNewTaDetailsOnJiraProcessor;
	private final InterviewCancellationActivityManager interviewCancellationActivityManager;
	private final FreeBookedTaSlotForCancelledInterviewProcessor freeBookedSlotForCancelledInterview;
	private final SearchNewInterviewTaProcessor searchNewInterviewTaProcessor;
	private final CommunicationToTaProcessor communicationToTaProcessor;
	private final UpdateTaCalendarInviteProcessor updateTaCalendarInviteProcessor;
	private final TACalendarInviteCancellationProcessor taCalendarInviteCancellationProcessor;

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void cancelCalendarInviteOfTA() throws Exception {
		this.interviewCancellationActivityManager
				.handle(
						CANCEL_CALENDAR_INVITE_OF_TA,
						this.taCalendarInviteCancellationProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void freeTaSlot() throws Exception {
		this.interviewCancellationActivityManager.handle(FREE_TA_SLOT_ACTIVITY_NAME,
				this.freeBookedSlotForCancelledInterview);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void searchInterviewForTaReassignment() throws Exception {
		this.interviewCancellationActivityManager.handle(SEARCH_NEW_INTERVIEW_FOR_TA,
				this.searchNewInterviewTaProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void reassignInterviewToTa() throws Exception {
		this.interviewCancellationActivityManager.handle(ASSIGNMENT_OF_NEW_INTERVIEW_TO_TA_ACTIVITY_NAME,
				this.assignNewInterviewToTaProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void updateNewTaDetailsOnJira() throws Exception {
		this.interviewCancellationActivityManager.handle(UPDATE_NEW_TA_DETAILS_ON_JIRA,
				this.updateNewTaDetailsOnJiraProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void sendReassignmentMailToTa() throws Exception {
		this.interviewCancellationActivityManager.handle(SEND_REASSIGNMENT_MAIL_TO_TA, this.communicationToTaProcessor);
	}

	@Scheduled(fixedDelayString = "${scheduled.fixedDelay}")
	public void sendReassignmentInviteToTa() throws Exception {
		this.interviewCancellationActivityManager.handle(SEND_REASSIGNMENT_INVITE_TO_TA,
				this.updateTaCalendarInviteProcessor);
	}
}
