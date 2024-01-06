/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.match_interviewers;

import static org.junit.Assert.*;

import com.barraiser.onboarding.common.TestingUtil;
import com.barraiser.onboarding.scheduling.match_interviewers.data.InterviewersSortingProcessorTestData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewerData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewersPerDayData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewersSortingProcessor;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersData;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(MockitoJUnitRunner.class)
public class InterviewersSortingProcessorTest {

	@Spy
	private ObjectMapper objectMapper;

	@InjectMocks
	private InterviewersSortingProcessor interviewersSortingProcessor;

	@InjectMocks
	private TestingUtil testingUtil;

	@Test
	public void sortingTest() throws IOException {

		final InterviewersSortingProcessorTestData testData = testingUtil.getTestingData(
				"src/test/resources/json_data_files/InterviewerSortingProcessorTestDataJson.json",
				InterviewersSortingProcessorTestData.class);

		final InterviewersPerDayData interviewersPerDayData1 = new InterviewersPerDayData();
		interviewersPerDayData1.setInterviewers(testData.getDayWiseInterviewers().get(1));
		final InterviewersPerDayData interviewersPerDayData2 = new InterviewersPerDayData();
		interviewersPerDayData2.setInterviewers(testData.getDayWiseInterviewers().get(2));
		final InterviewersPerDayData interviewersPerDayData3 = new InterviewersPerDayData();
		interviewersPerDayData3.setInterviewers(testData.getDayWiseInterviewers().get(3));
		final InterviewersPerDayData interviewersPerDayData4 = new InterviewersPerDayData();
		interviewersPerDayData4.setInterviewers(testData.getDayWiseInterviewers().get(4));
		final InterviewersPerDayData interviewersPerDayData5 = new InterviewersPerDayData();
		interviewersPerDayData5.setInterviewers(testData.getDayWiseInterviewers().get(5));
		final InterviewersPerDayData interviewersPerDayData6 = new InterviewersPerDayData();
		interviewersPerDayData6.setInterviewers(testData.getDayWiseInterviewers().get(6));
		InterviewersPerDayData interviewersPerDayData7 = new InterviewersPerDayData();
		interviewersPerDayData7.setInterviewers(testData.getDayWiseInterviewers().get(7));
		final List<InterviewersPerDayData> interviewersPerDayDataList = List.of(
				interviewersPerDayData1,
				interviewersPerDayData2,
				interviewersPerDayData3,
				interviewersPerDayData4,
				interviewersPerDayData5,
				interviewersPerDayData6,
				interviewersPerDayData7);
		final MatchInterviewersData data = new MatchInterviewersData();
		data.setInterviewersPerDayDataList(interviewersPerDayDataList);
		data.setDuplicateExperts(testData.getDuplicateInterviewers());
		this.interviewersSortingProcessor.process(data);
		testData.getDayWiseInterviewersResult()
				.forEach(
						(x, y) -> assertEquals(
								y,
								interviewersPerDayDataList
										.get(x - 1)
										.getInterviewers()
										.stream()
										.map(InterviewerData::getId)
										.collect(Collectors.toList())));
	}
}
