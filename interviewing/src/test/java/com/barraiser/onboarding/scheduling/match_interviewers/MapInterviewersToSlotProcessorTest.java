/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.match_interviewers;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.availability.AvailabilityManager;
import com.barraiser.onboarding.common.TestingUtil;
import com.barraiser.onboarding.scheduling.match_interviewers.data.DataPrepDayWiseProcessorTestData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewerData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewersPerDayData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MapInterviewersToSlotProcessor;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersData;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
public class MapInterviewersToSlotProcessorTest {

	@Spy
	private ObjectMapper objectMapper;

	@Mock
	private DateUtils dateUtils;

	@Mock
	private AvailabilityManager availabilityManager;

	@InjectMocks
	private MapInterviewersToSlotProcessor mapInterviewersToSlotProcessor;

	@InjectMocks
	private TestingUtil testingUtil;

	@Test
	public void formatInterviewersToDateWiseInterviewersTest() throws IOException {

		final DataPrepDayWiseProcessorTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/DataPrepDayWiseToHaveZeroBookedSlots.json",
				DataPrepDayWiseProcessorTestData.class);
		final MatchInterviewersData data = new MatchInterviewersData();
		data.setAvailabilityStartDate(1623609000L);
		data.setAvailabilityEndDate(1623961800L);
		testData.getLongToDate()
				.forEach(
						(x, y) -> when(this.dateUtils.getFormattedDateString(
								x, null, DateUtils.DATE_IN_YYYY_MM_DD_FORMAT))
										.thenReturn(y));
		data.setInterviewers(testData.getInterviewers());
		data.setInterviewersId(
				testData.getInterviewers().stream()
						.map(x -> x.getId())
						.collect(Collectors.toList()));
		when(this.availabilityManager.getBookedSlots(
				data.getInterviewersId(), 1623609000L, 1623961800L))
						.thenReturn(testData.getBookedSlotsPerInterviewer());
		this.mapInterviewersToSlotProcessor.process(data);
		assertEquals(5, data.getInterviewersPerDayDataList().size());
	}

	@Test
	public void formatInterviewersToDateWiseInterviewersTestWithNonZeroBookedSlots()
			throws IOException {

		final DataPrepDayWiseProcessorTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/DataPrepDayWiseNonZeroBookedSlots.json",
				DataPrepDayWiseProcessorTestData.class);
		final MatchInterviewersData data = new MatchInterviewersData();
		data.setAvailabilityStartDate(1623609000L);
		data.setAvailabilityEndDate(1623961800L);
		testData.getLongToDate()
				.forEach(
						(x, y) -> when(this.dateUtils.getFormattedDateString(
								x, null, DateUtils.DATE_IN_YYYY_MM_DD_FORMAT))
										.thenReturn(y));
		data.setInterviewers(testData.getInterviewers());
		data.setInterviewersId(
				testData.getInterviewers().stream()
						.map(x -> x.getId())
						.collect(Collectors.toList()));
		when(this.availabilityManager.getBookedSlots(
				data.getInterviewersId(), 1623609000L, 1623961800L))
						.thenReturn(testData.getBookedSlotsPerInterviewer());
		this.mapInterviewersToSlotProcessor.process(data);
		assertEquals(
				testData.getBookedSlotsPerDayPerInterviewer().size(),
				data.getInterviewersPerDayDataList().size());
		for (InterviewersPerDayData interviewersPerDayData : data.getInterviewersPerDayDataList()) {
			int index = 0;
			for (InterviewerData interviewer : interviewersPerDayData.getInterviewers()) {
				assertEquals(
						testData.getBookedSlotsPerDayPerInterviewer()
								.get(interviewersPerDayData.getDate())
								.get(index)
								.getSlotsBookedOnADay(),
						interviewer.getSlotsBookedOnADay());
				index++;
			}
		}
	}
}
