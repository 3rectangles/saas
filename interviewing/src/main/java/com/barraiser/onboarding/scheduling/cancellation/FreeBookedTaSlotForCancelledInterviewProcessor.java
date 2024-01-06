/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.availability.AvailabilityManager;
import com.barraiser.onboarding.availability.DTO.BookedSlotDTO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class FreeBookedTaSlotForCancelledInterviewProcessor implements CancellationProcessor {
	private final AvailabilityManager availabilityManager;

	@Override
	public void process(final CancellationProcessingData data) {
		if (!data.getIsTaAutoAllocationEnabled())
			return;
		final Long startTimeForInterview = data.getPreviousStateOfCancelledInterview().getStartDate();
		final Long endTimeForInterview = data.getPreviousStateOfCancelledInterview().getEndDate();
		final BookedSlotDTO bookedSlot = this.availabilityManager.findInterviewingBookedSlot(
				data.getPreviousStateOfCancelledInterview().getTaggingAgent(), startTimeForInterview,
				endTimeForInterview);
		this.availabilityManager.freeBookedSlot(bookedSlot);
	}
}
