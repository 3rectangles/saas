/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.match_interviewers;

import com.barraiser.onboarding.common.TestingUtil;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.scheduling.match_interviewers.data.GetAllUnusedInterviewersTestData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.GetAllUnusedInterviewersProcessor;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetAllUnusedInterviewersProcessorTest {
	@Spy
	private ObjectMapper objectMapper;

	@Mock
	private InterViewRepository interViewRepository;

	@InjectMocks
	private GetAllUnusedInterviewersProcessor getAllUnusedInterviewersProcessor;

	@InjectMocks
	private TestingUtil testingUtil;

	@Test
	public void getAllUnusedInterviewersTest() throws IOException {

		final GetAllUnusedInterviewersTestData testData = testingUtil
				.getTestingData("src/test/resources/json_data_files/GetAllUnusedInterviewersTestDataJson.json",
						GetAllUnusedInterviewersTestData.class);
		when(this.interViewRepository.findAllByEvaluationId(any()))
				.thenReturn(testData.getInterviews());
		final MatchInterviewersData data = new MatchInterviewersData();
		data.setInterviewers(testData.getInterviewers());
		this.getAllUnusedInterviewersProcessor.process(data);
		assertEquals(7, data.getInterviewers().size());
	}
}
