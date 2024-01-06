/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability;

import com.barraiser.common.graphql.types.Slot;
import com.barraiser.onboarding.dal.AvailabilityDAO;
import org.springframework.stereotype.Component;

@Component
public class SlotMapper {

	public Slot toSlot(final AvailabilityDAO availabilityDAO) {

		return Slot.builder()
				.userId(availabilityDAO.getUserId())
				.startDate(availabilityDAO.getStartDate())
				.endDate(availabilityDAO.getEndDate())
				.maximumNumberOfInterviews(availabilityDAO.getMaximumNumberOfInterviews())
				.build();
	}
}
