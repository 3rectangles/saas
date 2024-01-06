/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.match_interviewers;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.availability.AvailabilityManager;
import com.barraiser.onboarding.availability.DTO.BookedSlotDTO;
import com.barraiser.onboarding.common.TestingUtil;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.scheduling.scheduling.OverBookingThresholdCalculator;
import com.barraiser.onboarding.scheduling.match_interviewers.data.SlotToInterviewerAssignmentProcessorTestData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewersPerDayData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.SlotToPriorityInterviewerAssignmentProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.*;

@RunWith(MockitoJUnitRunner.class)
public class SlotToPriorityInterviewerAssignmentProcessorTest {
	@Spy
	private ObjectMapper objectMapper;

	@Mock
	private AvailabilityManager availabilityManager;

	@Mock
	private DateUtils dateUtils;

	@InjectMocks
	private SlotToPriorityInterviewerAssignmentProcessor slotToPriorityInterviewerAssignmentProcessor;

	@Mock
	private InterviewStructureRepository interviewStructureRepository;

	@InjectMocks
	private TestingUtil testingUtil;

	@Mock
	private OverBookingThresholdCalculator overBookingThresholdCalculator;

	@Test
	public void fetchDayWiseSlotsTest() throws IOException {

		final SlotToInterviewerAssignmentProcessorTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/SlotToInterviewerAssingmentProcessorTestJson.json",
				SlotToInterviewerAssignmentProcessorTestData.class);

		testData.getLongToDate()
				.forEach(
						(x, y) -> when(this.dateUtils.getFormattedDateString(
								x, null, DateUtils.DATE_IN_YYYY_MM_DD_FORMAT))
										.thenReturn(y));

		List<InterviewersPerDayData> interviewersPerDayDataList = new ArrayList<>();
		testData.getDateWiseInterviewers()
				.forEach(
						(x, y) -> {
							InterviewersPerDayData interviewersPerDayData = new InterviewersPerDayData();
							interviewersPerDayData.setDate(x);
							interviewersPerDayData.setInterviewers(y);
							interviewersPerDayDataList.add(interviewersPerDayData);
						});

		List<AvailabilityDAO> freeSlots = testData.getFreeSlots();

		when(this.availabilityManager.getAvailableSlotsOfAllInterviewers(any(), any(), any()))
				.thenReturn(freeSlots);

		when(this.availabilityManager.getOverlappingBookedSlotsCount(any(), any())).thenReturn(0);
		testData.getEpochTo15thMinuteCeil()
				.forEach((x, y) -> when(this.dateUtils.getEpochTo15ThMinuteCeil(x)).thenReturn(y));
		for (int i = 0; i < testData.getAvailableSlotsPerInterviewer().size(); i++) {
			final int j = i;
			doReturn(testData.getAvailableSlotsPerInterviewer().get("" + (i + 1)))
					.when(this.availabilityManager)
					.splitSlots(
							argThat(arg -> arg.getUserId().equals(freeSlots.get(j).getUserId())),
							eq(60L));
		}

		Map<String, List<BookedSlotDTO>> bookedSlots = testData.getBookedSlotsPerInterviewer();
		MatchInterviewersData data = new MatchInterviewersData();
		data.setInterviewersPerDayDataList(interviewersPerDayDataList);
		data.setAvailabilityStartDate(1623717000L);
		data.setAvailabilityEndDate(1624059000L);
		data.setBookedSlotsPerInterviewer(bookedSlots);
		data.setExpertJoiningTime(0);
		data.setDurationOfInterview(60L);
		data.setInterviewersId(
				List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"));
		data.setDuplicateExperts(testData.getDuplicateExperts());

		when(this.overBookingThresholdCalculator.getOverBookingThresholdForExpert(any()))
				.thenReturn(0.5);
		when(this.availabilityManager.getBookedSlots(any(), any(), any()))
				.thenReturn(testData.getBookedSlotsPerDuplicateExpert());
		this.slotToPriorityInterviewerAssignmentProcessor.process(data);

		testData.getSlotToInterviewer()
				.forEach((x, y) -> assertEquals(y, data.getSlotInterviewerMapping().get(x)));
	}
}
