/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.match_interviewers;

import com.barraiser.onboarding.common.TestingUtil;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.scheduling.match_interviewers.data.FilterSlotsTestData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.FilterSlotsProcessor;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FilterSlotsProcessorTest {
	@Spy
	private ObjectMapper objectMapper;
	@Mock
	private InterviewUtil interviewUtil;
	@Mock
	private InterViewRepository interViewRepository;
	@InjectMocks
	private FilterSlotsProcessor filterSlotsProcessor;
	@InjectMocks
	private TestingUtil testingUtil;

	@Test
	public void shouldReturnNoInterviewsForPartialOverlap() throws IOException {
		final FilterSlotsTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/FilterForPartialOverlapOfSlotOfIntervieweeTestDataJson.json",
				FilterSlotsTestData.class);
		final MatchInterviewersData data = new MatchInterviewersData();
		data.setInterviewId("1");
		data.setSlotInterviewerMapping(testData.getSlotsToInterviewer());
		data.setDurationOfInterview(60L);
		data.setAvailabilityStartDate(1637519400L);
		data.setAvailabilityEndDate(1637605800L);
		when(this.interViewRepository.findById("1"))
				.thenReturn(Optional.of(InterviewDAO.builder().intervieweeId("2").build()));
		when(this.interviewUtil.getOverlappingInterviewsForCandidate("2", 1637519400L, 1637605800L))
				.thenReturn(testData.getInterviewsOfInterviewee());
		this.filterSlotsProcessor.process(data);
		data.getSlotInterviewerMapping()
				.forEach((x, y) -> assertEquals(testData.getExpectedSlotsToInterviewer().get(x), y));
	}

	@Test
	public void shouldReturnInterviewsIfNoOverlapInterviewsPresent() throws IOException {
		final FilterSlotsTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/FilterForNoOverlappingInterviewsOfIntervieweeTestDataJson.json",
				FilterSlotsTestData.class);
		final MatchInterviewersData data = new MatchInterviewersData();
		data.setInterviewId("1");
		data.setSlotInterviewerMapping(testData.getSlotsToInterviewer());
		data.setDurationOfInterview(60L);
		data.setAvailabilityStartDate(1637519400L);
		data.setAvailabilityEndDate(1637605800L);
		when(this.interViewRepository.findById("1"))
				.thenReturn(Optional.of(InterviewDAO.builder().intervieweeId("2").build()));
		when(this.interviewUtil.getOverlappingInterviewsForCandidate("2", 1637519400L, 1637605800L))
				.thenReturn(testData.getInterviewsOfInterviewee());
		this.filterSlotsProcessor.process(data);
		data.getSlotInterviewerMapping()
				.forEach((x, y) -> assertEquals(testData.getExpectedSlotsToInterviewer().get(x), y));
	}

	@Test
	public void shouldNotReturnInterviewsIfCompleteOverlapInterviewsPresent() throws IOException {
		final FilterSlotsTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/FilterForCompleteOverlapOfInterviewsTestDataJson.json",
				FilterSlotsTestData.class);
		final MatchInterviewersData data = new MatchInterviewersData();
		data.setInterviewId("1");
		data.setSlotInterviewerMapping(testData.getSlotsToInterviewer());
		data.setDurationOfInterview(60L);
		data.setAvailabilityStartDate(1637519400L);
		data.setAvailabilityEndDate(1637605800L);
		when(this.interViewRepository.findById("1"))
				.thenReturn(Optional.of(InterviewDAO.builder().intervieweeId("2").build()));
		when(this.interviewUtil.getOverlappingInterviewsForCandidate("2", 1637519400L, 1637605800L))
				.thenReturn(testData.getInterviewsOfInterviewee());
		this.filterSlotsProcessor.process(data);
		data.getSlotInterviewerMapping()
				.forEach((x, y) -> assertEquals(testData.getExpectedSlotsToInterviewer().get(x), y));
	}

	@Test
	public void shouldReturnInterviewsIfNoOverlapPresent() throws IOException {
		final FilterSlotsTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/FilterForNoOverlapInterviewsTestDataJson.json",
				FilterSlotsTestData.class);
		final MatchInterviewersData data = new MatchInterviewersData();
		data.setInterviewId("1");
		data.setSlotInterviewerMapping(testData.getSlotsToInterviewer());
		data.setDurationOfInterview(60L);
		data.setAvailabilityStartDate(1637519400L);
		data.setAvailabilityEndDate(1637605800L);
		when(this.interViewRepository.findById("1"))
				.thenReturn(Optional.of(InterviewDAO.builder().intervieweeId("2").build()));
		when(this.interviewUtil.getOverlappingInterviewsForCandidate("2", 1637519400L, 1637605800L))
				.thenReturn(testData.getInterviewsOfInterviewee());
		this.filterSlotsProcessor.process(data);
		data.getSlotInterviewerMapping()
				.forEach((x, y) -> assertEquals(testData.getExpectedSlotsToInterviewer().get(x), y));
	}

}
