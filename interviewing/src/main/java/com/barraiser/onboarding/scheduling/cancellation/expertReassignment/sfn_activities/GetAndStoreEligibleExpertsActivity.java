/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment.sfn_activities;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertAllocatorData;
import com.barraiser.onboarding.scheduling.scheduling.EligibleExpertsProcessor;
import com.barraiser.onboarding.scheduling.scheduling.MatchInterviewersDataHelper;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.EligibleInterviewersFetcher;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewerData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class GetAndStoreEligibleExpertsActivity implements ExpertAllocatorSfnActivity {
	public static final String GET_AND_STORE_ELIGIBLE_EXPERTS_ACTIVITY_NAME = "get-and-store-eligible-experts";
	public static final String DYNAMO_TIME_TO_WAIT_BEFORE_INFORMING_OPS_IN_MINUTES = "time-to-wait-before-informing-duplicate-expert-to-ops-scheduling";
	public static final String TIMEZONE_UTC = "UTC";

	private final InterViewRepository interViewRepository;
	private final EligibleExpertsProcessor eligibleExpertsProcessor;
	private final DateUtils dateUtils;
	private final DynamicAppConfigProperties appConfigProperties;
	private final ObjectMapper objectMapper;
	private final MatchInterviewersDataHelper matchInterviewersDataHelper;
	private final EligibleInterviewersFetcher eligibleInterviewersFetcher;

	@Override
	public String name() {
		return GET_AND_STORE_ELIGIBLE_EXPERTS_ACTIVITY_NAME;
	}

	@Override
	public ExpertAllocatorData process(final String input) throws Exception {
		final ExpertAllocatorData data = objectMapper.readValue(input, ExpertAllocatorData.class);
		final InterviewDAO interviewDAO = this.interViewRepository.findById(data.getInterviewId()).get();
		final List<String> interviewerIds = this
				.fetchEligibleExpertsForInterview(interviewDAO.getId());
		this.eligibleExpertsProcessor.storeEligibleExpertsForInterview(interviewDAO, interviewerIds);
		data.setTimestampToWaitUntil(
				this.dateUtils.getFormattedDateString(
						data.getStartDate()
								- (long) this.appConfigProperties.getInt(
										DYNAMO_TIME_TO_WAIT_BEFORE_INFORMING_OPS_IN_MINUTES)
										* 60,
						TIMEZONE_UTC,
						DateUtils.DATEFORMAT_ISO_8601));
		return data;
	}

	public List<String> fetchEligibleExpertsForInterview(final String interviewId) throws IOException {
		final MatchInterviewersData data = this.matchInterviewersDataHelper.prepareDataForInterviewSlots(interviewId);
		this.eligibleInterviewersFetcher.populateEligibleInterviewersForOverbooking(data);
		return data.getInterviewers().stream()
				.map(InterviewerData::getId)
				.collect(Collectors.toList());
	}
}
