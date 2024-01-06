/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.payment.expert;

import com.barraiser.onboarding.dal.ExpertDAO;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStructureDAO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class InterviewPaymentCalculationData {
	private String interviewId;
	private String interviewStatus;
	private Integer rescheduleCount;
	private String interviewerId;
	private String cancellationReasonId;
	private Long cancellationTime;
	private InterviewDAO interview;
	private ExpertDAO expert;
	private InterviewStructureDAO interviewStructure;
	private Long interviewStartDate;
	private Long feedbackSubmissionTime;
}
