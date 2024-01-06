/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment;

import com.barraiser.onboarding.availability.AvailabilityManager;
import com.barraiser.onboarding.scheduling.expert_deallocation.dto.ExpertDeAllocatorData;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
@AllArgsConstructor
public class ReleaseExpertAvailabilityProcessor implements ExpertDeAllocationProcessor {
	private final AvailabilityManager availabilityManager;

	@Override
	public void process(final ExpertDeAllocatorData data) throws IOException {
		if (data.getIsInterviewCancelledByExpert()) {
			this.availabilityManager.removeBufferInBookedSlot(data.getBookedSlot());
		} else {
			this.availabilityManager.freeBookedSlot(data.getBookedSlot());
		}
	}
}
