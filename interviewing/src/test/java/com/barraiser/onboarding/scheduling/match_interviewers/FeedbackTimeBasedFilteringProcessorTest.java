/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.match_interviewers;

import com.barraiser.onboarding.common.TestingUtil;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.scheduling.match_interviewers.data.FeedbackTimeBasedFilteringProcessorTestData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.FeedbackTimeBasedFilteringProcessor;
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

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FeedbackTimeBasedFilteringProcessorTest {

	@Spy
	private ObjectMapper objectMapper;

	@Mock
	private InterViewRepository interViewRepository;

	@InjectMocks
	private FeedbackTimeBasedFilteringProcessor feedbackTimeBasedFilteringProcessor;

	@InjectMocks
	private TestingUtil testingUtil;

	@Test
	public void shouldFilterInterviewersWithPendingFeedback() throws IOException {

		final FeedbackTimeBasedFilteringProcessorTestData testData = testingUtil.getTestingData(
				"src/test/resources/json_data_files/ShouldFilterInterviewersWithPendingFeedback.json",
				FeedbackTimeBasedFilteringProcessorTestData.class);
		MatchInterviewersData data = new MatchInterviewersData();
		data.setInterviewers(testData.getInterviewers());
		when(this.interViewRepository.findAllByStatusAndInterviewerIdIn("pending_feedback_submission",
				data.getInterviewers().stream().map(x -> x.getId()).collect(Collectors.toList())))
						.thenReturn(testData.getInterviews());
		this.feedbackTimeBasedFilteringProcessor.process(data);
		assertEquals(3, data.getInterviewers().size());
	}

	@Test
	public void shouldNotFilterInterviewsForNullEndDate() throws IOException {

		final FeedbackTimeBasedFilteringProcessorTestData testData = testingUtil.getTestingData(
				"src/test/resources/json_data_files/shouldNotFilterInterviewsForNullEndDateJson.json",
				FeedbackTimeBasedFilteringProcessorTestData.class);
		final MatchInterviewersData data = new MatchInterviewersData();
		data.setInterviewers(testData.getInterviewers());
		when(this.interViewRepository.findAllByStatusAndInterviewerIdIn("pending_feedback_submission",
				data.getInterviewers().stream().map(x -> x.getId()).collect(Collectors.toList())))
						.thenReturn(testData.getInterviews());
		this.feedbackTimeBasedFilteringProcessor.process(data);
		assertEquals(6, data.getInterviewers().size());
	}
}
