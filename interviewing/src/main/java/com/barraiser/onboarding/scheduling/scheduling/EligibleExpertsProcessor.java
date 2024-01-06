/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewToEligibleExpertsDAO;
import com.barraiser.onboarding.dal.InterviewToEligibleExpertsRepository;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.EligibleInterviewersFetcher;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.InterviewerData;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersData;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.barraiser.common.utilities.DateUtils.TIMEZONE_UTC;

@Log4j2
@Component
@AllArgsConstructor
public class EligibleExpertsProcessor implements SchedulingProcessor {
	public static final String DYNAMO_TIME_TO_WAIT_BEFORE_INFORMING_OPS_IN_MINUTES = "time-to-wait-before-informing-duplicate-expert-to-ops-scheduling";

	private final InterviewToEligibleExpertsRepository interviewToEligibleExpertsRepository;
	private final MatchInterviewersDataHelper matchInterviewersDataHelper;
	private final DateUtils dateUtils;
	private final DynamicAppConfigProperties appConfigProperties;
	private final InterViewRepository interViewRepository;
	private final EligibleInterviewersFetcher eligibleInterviewersFetcher;

	@Override
	@Transactional
	public void process(final SchedulingProcessingData data) throws Exception {
		final InterviewDAO interviewDAO = this.interViewRepository.findById(data.getInput().getInterviewId()).get();
		final List<String> interviewerIds = this.fetchEligibleExpertsForInterview(interviewDAO.getId());
		this.storeEligibleExpertsForInterview(interviewDAO, interviewerIds);
		data.setTimestampToWaitUntil(
				this.dateUtils.getFormattedDateString(
						data.getInput().getStartDate()
								- (long) this.appConfigProperties.getInt(
										DYNAMO_TIME_TO_WAIT_BEFORE_INFORMING_OPS_IN_MINUTES)
										* 60,
						TIMEZONE_UTC,
						DateUtils.DATEFORMAT_ISO_8601));
	}

	public List<String> fetchEligibleExpertsForInterview(final String interviewId) throws IOException {
		final MatchInterviewersData data = this.matchInterviewersDataHelper.prepareDataForInterviewSlots(interviewId);
		this.eligibleInterviewersFetcher.populateEligibleInterviewers(data);
		return data.getInterviewers().stream()
				.map(InterviewerData::getId)
				.collect(Collectors.toList());
	}

	public void storeEligibleExpertsForInterview(
			final InterviewDAO interviewDAO, final List<String> interviewerIds) {
		final List<InterviewToEligibleExpertsDAO> interviewToEligibleExpertsDAOs = interviewerIds.stream()
				.map(
						interviewerId -> InterviewToEligibleExpertsDAO.builder()
								.id(UUID.randomUUID().toString())
								.interviewId(interviewDAO.getId())
								.interviewerId(interviewerId)
								.rescheduleCount(interviewDAO.getRescheduleCount())
								.build())
				.collect(Collectors.toList());
		this.deletePreviousEligibleExperts(interviewDAO.getId());
		this.interviewToEligibleExpertsRepository.saveAll(interviewToEligibleExpertsDAOs);
	}

	private void deletePreviousEligibleExperts(final String interviewId) {
		List<InterviewToEligibleExpertsDAO> interviewToEligibleExpertsDAOs = this.interviewToEligibleExpertsRepository
				.findAllByInterviewId(interviewId);
		interviewToEligibleExpertsDAOs = interviewToEligibleExpertsDAOs.stream()
				.map(x -> x.toBuilder().deletedOn(Instant.now().getEpochSecond()).build())
				.collect(Collectors.toList());
		this.interviewToEligibleExpertsRepository.saveAll(interviewToEligibleExpertsDAOs);
	}
}
