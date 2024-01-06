/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.match_interviewers.data;

import com.barraiser.onboarding.availability.DTO.BookedSlotDTO;
import com.barraiser.onboarding.dal.AvailabilityDAO;
import com.barraiser.onboarding.dal.BookedSlotsDAO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AvailabilityManagerTestData {

	private Map<String, List<AvailabilityDAO>> availabilityPerInterviewer;
	private Map<String, List<BookedSlotDTO>> bookedSlotsPerInterviewerMidOverlap;
	private Map<String, List<BookedSlotDTO>> bookedSlotsPerInterviewerRightOverlap;
	private Map<String, List<BookedSlotDTO>> bookedSlotsPerInterviewerLeftOverlap;
	private Map<String, List<AvailabilityDAO>> calculatedAvailabilityOfInterviewer;
	private Map<String, List<BookedSlotDTO>> calculatedBookedSlotOfInterviewer;
	private Map<Long, Long> epochTo15thMinuteCeil;
	private List<AvailabilityDAO> freeSlots;
}
