/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.scheduling.expert_deallocation.dto.ExpertDeAllocatorData;
import com.barraiser.onboarding.scheduling.expert_deallocation.ExpertDeallocator;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Component
@AllArgsConstructor
public class CancelInterviewProcessor {
	private final InterviewUpdationForCancellationProcessor interviewUpdationForCancellationProcessor;
	private final InterviewCreationInDatabaseProcessor interviewCreationInDatabaseProcessor;
	private final ExpertDeallocator expertDeallocator;

	@Transactional
	public void cancelInterview(final CancellationProcessingData data) throws Exception {
		this.interviewUpdationForCancellationProcessor.process(data);

		final ExpertDeAllocatorData expertDeAllocatorData = ExpertDeAllocatorData.builder()
				.interviewId(data.getInterviewId())
				.deAllocationReason(data.getInterviewToBeCancelled().getCancellationReasonId())
				.deallocatedBy(data.getUserCancellingTheInterview())
				.deAllocationTime(data.getCancellationTimeOfInterview())
				.build();
		this.expertDeallocator.deallocateExpertForInterview(expertDeAllocatorData);
		data.setBuffer(expertDeAllocatorData.getBookedSlot() != null ? expertDeAllocatorData.getBookedSlot().getBuffer()
				: null);

		this.interviewCreationInDatabaseProcessor.process(data);
	}
}
