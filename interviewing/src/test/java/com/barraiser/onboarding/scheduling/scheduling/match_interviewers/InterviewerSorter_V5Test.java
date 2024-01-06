/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.onboarding.common.TestingUtil;
import com.barraiser.onboarding.scheduling.match_interviewers.data.InterviewerSorterTestData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class InterviewerSorter_V5Test {

	@InjectMocks
	private InterviewerSorter_V5 interviewersSorter_v5;
	@Spy
	private ObjectMapper objectMapper;
	@InjectMocks
	private TestingUtil testingUtil;

	@Test
	public void shouldGiveHigherPriorityIfExpertIsDemoEligibleAndInterviewIsDemo() throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortForDemoInterviews.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v5.sort(testData.getInterviewers(), true,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldGiveHigherPriorityToExpertHavingHigherProficiencyIfNotDemoInterview() throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortBasisOnProficiency.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v5.sort(testData.getInterviewers(), false,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldGiveHigherPriorityToExpertHavingLowerBookedSlots() throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortBasisOnBookedSlotsTestData.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v5.sort(testData.getInterviewers(), false,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldGivePriorityConsideringAllCriteria() throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortBasisOnAllCriteriaTestDataJson.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v5.sort(testData.getInterviewers(), true,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldGivePriorityToExpertHavingHigherCost() throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortBasedOnCostTestData.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v5.sort(testData.getInterviewers(), false,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldGivePriorityToExpertLowerBookedSlotsInADay() throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortBasedOnBookedSlotsInADayTestData.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v5.sort(testData.getInterviewers(), false,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldGivePriorityToExpertLowerBookedSlotsInAWeek() throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortBasedOnBookedSlotsInADayTestData.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v5.sort(testData.getInterviewers(), false,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldGivePriorityToExpertHavingLowerMinCostIfFallbackEnabled() throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortBasedOnMinCostAndFallbackTestData.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v5.sort(testData.getInterviewers(), false,
				Boolean.TRUE);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}
}
