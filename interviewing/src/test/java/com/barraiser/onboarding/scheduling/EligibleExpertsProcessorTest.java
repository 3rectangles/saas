/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.InterviewToEligibleExpertsDAO;
import com.barraiser.onboarding.dal.InterviewToEligibleExpertsRepository;
import com.barraiser.onboarding.scheduling.scheduling.EligibleExpertsProcessor;
import com.barraiser.onboarding.scheduling.scheduling.MatchInterviewersDataHelper;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.GetEligibleInterviewersProcessor;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewerData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EligibleExpertsProcessorTest {
	@InjectMocks
	private EligibleExpertsProcessor eligibleExpertsProcessor;
	@Mock
	private InterviewToEligibleExpertsRepository interviewToEligibleExpertsRepository;
	@Mock
	private MatchInterviewersDataHelper matchInterviewersDataHelper;
	@Mock
	private GetEligibleInterviewersProcessor getEligibleInterviewersProcessor;
	@Mock
	private DateUtils dateUtils;
	@Mock
	private DynamicAppConfigProperties appConfigProperties;

	@Test
	public void shouldStoreListOfEligibleExperts() throws Exception {
		final List<InterviewerData> interviewerDataList = List.of(
				InterviewerData.builder().id("1").build(),
				InterviewerData.builder().id("2").build());
		final MatchInterviewersData matchInterviewersData = new MatchInterviewersData();
		matchInterviewersData.setInterviewers(interviewerDataList);
		when(this.matchInterviewersDataHelper.prepareDataForInterviewSlots(any()))
				.thenReturn(matchInterviewersData);
		this.getEligibleInterviewersProcessor.process(matchInterviewersData);
		final SchedulingProcessingData data = new SchedulingProcessingData();
		this.eligibleExpertsProcessor.process(data);
		final List<InterviewToEligibleExpertsDAO> expectedList = List.of(
				InterviewToEligibleExpertsDAO.builder()
						.interviewId("i-1")
						.interviewerId("1")
						.build(),
				InterviewToEligibleExpertsDAO.builder()
						.interviewId("i-1")
						.interviewerId("2")
						.build());
		verify(this.interviewToEligibleExpertsRepository)
				.saveAll(
						argThat(
								arg -> {
									for (final InterviewToEligibleExpertsDAO actual : arg) {
										expectedList.stream()
												.filter(
														y -> y.getInterviewerId()
																.equals(
																		actual
																				.getInterviewerId()))
												.findFirst()
												.get();
									}
									return true;
								}));
	}
}
