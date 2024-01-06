/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment;

import com.barraiser.onboarding.scheduling.expert_deallocation.dto.ExpertDeAllocatorData;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertReassignmentData;
import com.barraiser.onboarding.scheduling.expert_deallocation.ExpertDeallocator;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Log4j2
@Component
@AllArgsConstructor
public class DeAllocateExpertProcessor implements ExpertReassignmentProcessor {
	public static final String EXPERT_REASSIGNMENT = "EXPERT_REASSIGNMENT";
	private final ExpertDeallocator expertDeallocator;

	@Override
	public void process(final ExpertReassignmentData data) throws Exception {
		final ExpertDeAllocatorData expertDeAllocatorData = ExpertDeAllocatorData.builder()
				.interviewId(data.getInterviewId())
				.deAllocationReason(data.getReassignmentReason())
				.deallocatedBy(data.getReassignedBy())
				.deAllocationTime(Instant.now().getEpochSecond())
				.source(EXPERT_REASSIGNMENT)
				.build();

		this.expertDeallocator.deallocateExpertForInterview(expertDeAllocatorData);
	}
}
