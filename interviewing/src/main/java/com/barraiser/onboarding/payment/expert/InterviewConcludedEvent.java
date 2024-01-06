/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.payment.expert;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class InterviewConcludedEvent {
	private String interviewId;
	private String interviewStatus;
	private Integer rescheduleCount;
	private String interviewerId;
	private String cancellationReasonId;
	private Long cancellationTime;
	private Long interviewStartDate;
	private Long feedbackSubmissionTime;
}
