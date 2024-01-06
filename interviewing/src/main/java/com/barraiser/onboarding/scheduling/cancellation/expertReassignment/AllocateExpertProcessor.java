/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment;

import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertAllocatorData;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertReassignmentData;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class AllocateExpertProcessor implements ExpertReassignmentProcessor {
	public static final String ALLOCATED_BY_SYSTEM = "BarRaiser";
	private final ExpertAllocator expertAllocator;

	@Override
	public void process(final ExpertReassignmentData data) throws Exception {
		final ExpertAllocatorData expertAllocatorData = ExpertAllocatorData.builder()
				.interviewId(data.getInterviewId())
				.interviewerId(data.getInterviewerToRescheduleWith())
				.startDate(data.getInterview().getStartDate())
				.isOnlyCandidateChanged(false)
				.allocatedBy(ALLOCATED_BY_SYSTEM)
				.source(ExpertAllocationSource.AUTOMATIC_EXPERT_REASSIGNMENT.getValue())
				.build();
		this.expertAllocator.allocateExpertToInterview(expertAllocatorData);
	}
}
