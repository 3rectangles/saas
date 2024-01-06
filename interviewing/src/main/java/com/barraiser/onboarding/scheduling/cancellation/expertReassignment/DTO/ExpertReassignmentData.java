/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO;

import com.barraiser.onboarding.dal.InterviewDAO;
import lombok.Data;

@Data
public class ExpertReassignmentData {
	private String interviewId;
	private InterviewDAO interview;
	private Long cancellationRequestedTimeOfInterview;
	private String interviewerToRescheduleWith;
	private Boolean shouldExpertBeReassigned;
	private Boolean shouldExpertBeReassignedBySystem;
	private String timestampToWaitBeforeSendingRescheduledMail;
	private Boolean isCandidateSchedulingEnabled;
	private Boolean isInterviewSuccessfullyRescheduled;
	private String reassignedBy;
	private String reassignmentReason;
}
