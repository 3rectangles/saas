/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.FeedbackTimeBasedFilteringProcessor;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewerData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersData;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class FeedbackTimeBasedFilteringProcessorTest {

	@Mock
	private InterViewRepository interViewRepository;

	@InjectMocks
	private FeedbackTimeBasedFilteringProcessor feedbackTimeBasedFilteringProcessor;

	public static List<InterviewerData> interviewers = new ArrayList<>();

	@Test
	public void testProcess() {

		interviewers.add(InterviewerData.builder().id("1").build());
		interviewers.add(InterviewerData.builder().id("2").build());
		interviewers.add(InterviewerData.builder().id("3").build());
		interviewers.add(InterviewerData.builder().id("4").build());
		interviewers.add(InterviewerData.builder().id("5").build());
		interviewers.add(InterviewerData.builder().id("6").build());

		MatchInterviewersData data = new MatchInterviewersData();
		data.setInterviewers(interviewers);
		when(this.interViewRepository.findAllByStatusAndInterviewerIdIn("pending_feedback_submission",
				data.getInterviewers().stream().map(x -> x.getId()).collect(Collectors.toList())))
						.thenReturn(Arrays.asList(
								InterviewDAO.builder().interviewerId("1").actualEndDate(1623645000L).build(), // 14th 10
																												// am
								InterviewDAO.builder().interviewerId("2").endDate(1623645000L).build(), // 14th 10am
								InterviewDAO.builder().interviewerId("5").actualEndDate(1623731400L).build(), // 15th
																												// 10am
								InterviewDAO.builder().interviewerId("6").endDate(1623731400L).build(), // 15th 10am
								InterviewDAO.builder().interviewerId("3").actualEndDate(1623645000L)
										.endDate(1623731400L).build(), // 14th 10 am ,15th 10am
								InterviewDAO.builder().interviewerId("4").actualEndDate(1623731400L)
										.endDate(1623645000L).build()// 15th 10am, 14th 10am
						));
		this.feedbackTimeBasedFilteringProcessor.process(data);
		assertEquals(6, data.getInterviewers().size());
	}

	@Test
	public void testProcessForNull() {

		interviewers.add(InterviewerData.builder().id("1").build());
		interviewers.add(InterviewerData.builder().id("2").build());
		interviewers.add(InterviewerData.builder().id("3").build());
		interviewers.add(InterviewerData.builder().id("4").build());
		interviewers.add(InterviewerData.builder().id("5").build());
		interviewers.add(InterviewerData.builder().id("6").build());

		MatchInterviewersData data = new MatchInterviewersData();
		data.setInterviewers(interviewers);
		when(this.interViewRepository.findAllByStatusAndInterviewerIdIn("pending_feedback_submission",
				data.getInterviewers().stream().map(x -> x.getId()).collect(Collectors.toList())))
						.thenReturn(Arrays.asList(
								InterviewDAO.builder().interviewerId("1").build()));
		this.feedbackTimeBasedFilteringProcessor.process(data);
		assertEquals(6, data.getInterviewers().size());
	}

}
