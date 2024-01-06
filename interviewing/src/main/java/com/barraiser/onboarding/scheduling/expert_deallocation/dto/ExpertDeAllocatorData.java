/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.expert_deallocation.dto;

import com.barraiser.onboarding.availability.DTO.BookedSlotDTO;
import com.barraiser.onboarding.dal.BookedSlotsDAO;
import com.barraiser.onboarding.dal.InterviewDAO;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ExpertDeAllocatorData {
	private String interviewId;
	private InterviewDAO interview;
	private String deallocatedBy;
	private String originalInterviewerId;
	private InterviewDAO newInterviewThatExpertCanTake;
	private Boolean isInterviewCancelledByExpert;
	private Boolean isExpertDuplicate;
	private Long deAllocationTime;
	private String deAllocationReason;
	private BookedSlotDTO bookedSlot;
	private String source;
}
