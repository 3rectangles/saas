/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.match_interviewers;

import static org.mockito.Mockito.when;

import com.barraiser.common.graphql.types.InterviewSlots;
import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.common.TestingUtil;
import com.barraiser.onboarding.scheduling.match_interviewers.data.ResponseConstructionProcessorTestData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewersPerDayData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.ResponseConstructionProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ResponseConstructionProcessorTest {

	@Spy
	private ObjectMapper objectMapper;

	@Mock
	private DateUtils dateUtils;

	@InjectMocks
	private ResponseConstructionProcessor responseConstructionProcessor;

	@InjectMocks
	private TestingUtil testingUtil;

	@Test
	public void responseConstructionProcessorTest() throws IOException {

		final ResponseConstructionProcessorTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/ResponseContructionProcessorTestDataJson.json",
				ResponseConstructionProcessorTestData.class);

		testData.getLongToDate()
				.forEach(
						(x, y) -> when(this.dateUtils.getFormattedDateString(
								x, null, DateUtils.DATE_IN_YYYY_MM_DD_FORMAT))
										.thenReturn(y));

		final MatchInterviewersData data = new MatchInterviewersData();
		final InterviewersPerDayData interviewersPerDayData1 = new InterviewersPerDayData();
		interviewersPerDayData1.setDate("2021-06-21");
		interviewersPerDayData1.setInterviewers(testData.getDayWiseInterviewers().get(1));

		final InterviewersPerDayData interviewersPerDayData2 = new InterviewersPerDayData();
		interviewersPerDayData2.setDate("2021-06-22");
		interviewersPerDayData2.setInterviewers(testData.getDayWiseInterviewers().get(2));

		final InterviewersPerDayData interviewersPerDayData3 = new InterviewersPerDayData();
		interviewersPerDayData3.setDate("2021-06-23");
		interviewersPerDayData3.setInterviewers(testData.getDayWiseInterviewers().get(3));

		final InterviewersPerDayData interviewersPerDayData4 = new InterviewersPerDayData();
		interviewersPerDayData4.setDate("2021-06-24");
		interviewersPerDayData4.setInterviewers(testData.getDayWiseInterviewers().get(4));
		final List<InterviewersPerDayData> interviewersPerDayDataList = List.of(
				interviewersPerDayData1,
				interviewersPerDayData2,
				interviewersPerDayData3,
				interviewersPerDayData4);

		data.setSlotInterviewerMapping(testData.getSlotToInterviewer());
		data.setInterviewersPerDayDataList(interviewersPerDayDataList);
		this.responseConstructionProcessor.process(data);
		final List<InterviewSlots> interviewSlots = data.getInterviewSlots();
		interviewSlots.forEach(
				x -> System.out.println(
						x.getDate()
								+ " : "
								+ x.getPrioritySlots()
								+ " : "
								+ x.getAllSlots()));
	}
}
