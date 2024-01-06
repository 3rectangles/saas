/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.Interview;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
class InterviewDataFetcherTest {

	@InjectMocks
	private InterviewDataFetcher interviewDataFetcher;

	@Test
	public void sortInterviewsTest() {
		MockitoAnnotations.initMocks(this);

		Interview interview1 = Interview.builder()
				.orderIndex(1)
				.scheduledStartDate(1000L)
				.build();

		Interview interview2 = Interview.builder()
				.orderIndex(1)
				.scheduledStartDate(2000L)
				.build();

		Interview interview3 = Interview.builder()
				.orderIndex(2)
				.scheduledStartDate(1000L)
				.build();

		Interview interview4 = Interview.builder()
				.orderIndex(2)
				.scheduledStartDate(null)
				.build();

		// Test1
		final List<Interview> testList = new ArrayList<>(List.of(interview3, interview2, interview1));
		final List<Interview> sortedList = List.of(interview1, interview2, interview3);
		this.interviewDataFetcher.sortInterviews(testList);
		Assertions.assertEquals(sortedList, testList);

		// Test2
		final List<Interview> testList2 = new ArrayList<>(List.of(interview3, interview1, interview2));
		this.interviewDataFetcher.sortInterviews(testList2);
		Assertions.assertEquals(sortedList, testList2);

		// Test3
		final List<Interview> testList3 = new ArrayList<>(List.of(interview4, interview2, interview1, interview3));
		final List<Interview> sortedList2 = List.of(interview1, interview2, interview4, interview3);
		this.interviewDataFetcher.sortInterviews(testList3);
		Assertions.assertEquals(sortedList2, testList3);

		// Test4
		final List<Interview> testList4 = new ArrayList<>(List.of(interview4, interview2, interview1, interview3));
		final List<Interview> sortedList3 = List.of(interview1, interview2, interview4, interview3);
		this.interviewDataFetcher.sortInterviews(testList4);
		Assertions.assertEquals(sortedList3, testList4);

	}
}
