/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.sfn;

import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertAllocatorData;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.ExpertAllocationSource;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.ExpertAllocator;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ExpertAllocationToInterviewProcessor implements SchedulingProcessor {
	private final ExpertAllocator expertAllocator;

	@Override
	public void process(final SchedulingProcessingData data) throws Exception {
		final ExpertAllocatorData expertAllocatorData = ExpertAllocatorData.builder()
				.interviewId(data.getInput().getInterviewId())
				.interviewerId(data.getInput().getInterviewerId())
				.startDate(data.getInput().getStartDate())
				.isOnlyCandidateChanged(false)
				.allocatedBy(data.getUser().getUserName())
				.source(ExpertAllocationSource.INTERVIEW_SCHEDULING.getValue())
				.schedulingPlatform(data.getSchedulingPlatform())
				.build();
		this.expertAllocator.allocateExpertToInterview(expertAllocatorData);
	}
}
