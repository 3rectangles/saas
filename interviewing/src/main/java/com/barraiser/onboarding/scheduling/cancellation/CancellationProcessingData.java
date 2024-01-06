/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.dal.InterviewDAO;
import lombok.Data;

@Data
public class CancellationProcessingData {
	private String interviewId;
	private Integer interviewRescheduleCount;
	private InterviewDAO interviewToBeCancelled;
	private Long buffer;
	private InterviewDAO interviewThatExpertCanTake;
	private Boolean isOriginalInterviewScheduledWithDuplicate;
	private Boolean isTaAssigned;
	private InterviewDAO interviewForTaReassignment;
	private Boolean isTaAutoAllocationEnabled;
	private Long cancellationTimeOfInterview;
	private Boolean isInterviewCancelledByExpert;
	private String userCancellingTheInterview;
	private Boolean isNonReschedulableInterview;
	private String sourceOfCancellation;
	private InterviewDAO previousStateOfCancelledInterview;
	private String oldJiraKey;
	private String partnerId;
}
