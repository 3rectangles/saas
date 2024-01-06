/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.match_interviewers.data;

import com.barraiser.onboarding.availability.DTO.BookedSlotDTO;
import com.barraiser.onboarding.dal.AvailabilityDAO;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewerData;
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
public class SlotToInterviewerAssignmentProcessorTestData {

	private Map<String, List<AvailabilityDAO>> availableSlotsPerInterviewer;
	private Map<String, List<BookedSlotDTO>> bookedSlotsPerInterviewer;
	private Map<String, List<InterviewerData>> dateWiseInterviewers;
	private Map<Long, String> longToDate;
	private Map<Long, Long> epochTo15thMinuteCeil;
	private List<AvailabilityDAO> freeSlots;
	private Map<Long, String> slotToInterviewer;
	private List<InterviewerData> duplicateExperts;
	private Map<String, List<BookedSlotDTO>> bookedSlotsPerDuplicateExpert;
}
