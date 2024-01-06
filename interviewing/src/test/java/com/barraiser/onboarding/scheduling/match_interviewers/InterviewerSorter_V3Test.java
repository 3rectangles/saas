/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.match_interviewers;

import com.barraiser.onboarding.common.TestingUtil;
import com.barraiser.onboarding.payment.expert.ExpertPaymentUtil;
import com.barraiser.onboarding.scheduling.match_interviewers.data.InterviewerSorterTestData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewerData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewerSorter_V3;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class InterviewerSorter_V3Test {
	@InjectMocks
	private InterviewerSorter_V3 interviewersSorter_v3;
	@Spy
	private ObjectMapper objectMapper;
	@InjectMocks
	private TestingUtil testingUtil;
	@Spy
	private ExpertPaymentUtil expertPaymentUtil;

	@Test
	public void shouldGiveHigherPriorityToExpertsWith0WeeklyBookedInterviewsForNonDemoInterview() throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortForWeeklyBookedPriorityTestDataJson.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v3.sort(testData.getInterviewers(), false,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldGiveHigherPriorityToProficiencyForNonDemoInterview() throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortForHigherProficiencyTestDataJson.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v3.sort(testData.getInterviewers(), false,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldGiveHigherPriorityToNoBookedSlotsForNonDemoInterview() throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortForHigherPriorityToZeroBookedSlotsTestData.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v3.sort(testData.getInterviewers(), false,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldReturnPriorityListBasedOnProficiencyAndBookedSlotsForNonDemoInterview() throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortOnProficiencyAndBookedSlotTestData.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v3.sort(testData.getInterviewers(), false,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldReturnPriorityListBasedOnProficiencyAndBookedSlotsAndCostForNonDemoInterview()
			throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortOnProficiencyAndBookedSlotAndCost.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v3.sort(testData.getInterviewers(), false,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldReturnPriorityListBasedOnBookedSlotsAndCostForNonDemoInterview() throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortOnBookedSlotAndCost.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v3.sort(testData.getInterviewers(), false,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldReturnOriginalPriorityListIfAllHaveMaxFulfilmentFactorForNonDemoInterview() throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortOnMaxFulfilmentFactorTestData.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v3.sort(testData.getInterviewers(), false,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldReturnPriorityListIfOneHasGreaterThanMaxFulfilmentFactorForNonDemoInterview()
			throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortForGreaterThanFulfilmentFactorTestData.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v3.sort(testData.getInterviewers(), false,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldReturnPriorityListConsideringAllConditionsForNonDemoInterview() throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortAllConditionsTestData.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v3.sort(testData.getInterviewers(), false,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldReturnPriorityListOnRandomConditionsForNonDemoInterview() throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortRandomConditionsTestData.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v3.sort(testData.getInterviewers(), false,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldGiveHigherPriorityToExpertsWith0WeeklyBookedInterviewsForDemoInterview() throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortForWeeklyBookedPriorityTestDataJson.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v3.sort(testData.getInterviewers(), false,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldGiveHigherPriorityToProficiencyForDemoInterview() throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortForWeeklyBookedPriorityTestDataForDemoInterviewJson.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v3.sort(testData.getInterviewers(), false,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldGiveHigherPriorityToNoBookedSlotsForDemoInterview() throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortForHigherPriorityToZeroBookedSlotsTestDataForDemoInterview.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v3.sort(testData.getInterviewers(), false,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldReturnPriorityListBasedOnProficiencyAndBookedSlotsForDemoInterview() throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortOnProficiencyAndBookedSlotTestDataForDemoInterview.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v3.sort(testData.getInterviewers(), false,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldReturnPriorityListBasedOnProficiencyAndBookedSlotsAndCostForDemoInterview()
			throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortOnProficiencyAndBookedSlotAndCostForDemoInterview.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v3.sort(testData.getInterviewers(), false,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldReturnPriorityListBasedOnBookedSlotsAndCostForDemoInterview() throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortOnBookedSlotAndCostForDemoInterview.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v3.sort(testData.getInterviewers(), false,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldReturnOriginalPriorityListIfAllHaveMaxFulfilmentFactorForDemoInterview() throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortOnMaxFulfilmentFactorTestDataForDemoInterview.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v3.sort(testData.getInterviewers(), false,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldReturnPriorityListIfOneHasGreaterThanMaxFulfilmentFactorForDemoInterview()
			throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortForGreaterThanFulfilmentFactorTestDataForDemoInterview.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v3.sort(testData.getInterviewers(), false,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldReturnPriorityListConsideringAllConditionsForDemoInterview() throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortAllConditionsTestDataForDemoInterview.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v3.sort(testData.getInterviewers(), false,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

	@Test
	public void shouldReturnPriorityListOnRandomConditionsForDemoInterview() throws IOException {
		final InterviewerSorterTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortRandomConditionsTestDataForDemoInterview.json",
				InterviewerSorterTestData.class);
		final List<InterviewerData> sortedList = this.interviewersSorter_v3.sort(testData.getInterviewers(), false,
				null);
		final List<InterviewerData> expectedList = testData.getExpertListOfPriorityInterviewers();
		assertEquals(
				expectedList.stream().map(InterviewerData::getId).collect(Collectors.toList()),
				sortedList.stream().map(InterviewerData::getId).collect(Collectors.toList()));
	}

}
