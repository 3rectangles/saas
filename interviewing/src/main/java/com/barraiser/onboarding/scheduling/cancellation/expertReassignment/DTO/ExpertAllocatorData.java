/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingCommunicationData;
import com.barraiser.onboarding.scheduling.scheduling.ExpertSchedulingCommunicationData;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ExpertAllocatorData {
	private InterviewDAO interview;
	private String interviewerId;
	private String allocatedBy;
	private Long startDate;
	private Boolean isOnlyCandidateChanged;
	private ExpertSchedulingCommunicationData expertSchedulingCommunicationData;
	private SchedulingCommunicationData schedulingCommunicationData;
	private String interviewId;
	private String previousInterviewOfExpert;
	private String timestampToWaitUntil;
	private Boolean isExpertDuplicate;
	private String source;
	private String schedulingPlatform;

}
