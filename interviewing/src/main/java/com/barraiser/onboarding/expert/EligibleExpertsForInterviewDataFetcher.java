/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.expert;

import com.barraiser.common.graphql.input.GetEligibleInterviewersInput;
import com.barraiser.common.graphql.types.Interviewer;
import com.barraiser.onboarding.availability.AvailabilityManager;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStructureDAO;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.graphql.EligibleInterviewersOutput;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewStructureManager;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.scheduling.scheduling.MatchInterviewersDataHelper;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingSessionManager;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.EligibleInterviewersFetcher;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.MatchInterviewersData;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.ListUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class EligibleExpertsForInterviewDataFetcher implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final MatchInterviewersDataHelper matchInterviewersDataHelper;
	private final UserDetailsRepository userDetailsRepository;
	private final AvailabilityManager availabilityManager;
	private final InterViewRepository interViewRepository;
	private final InterviewStructureManager interviewStructureManager;
	private final EligibleInterviewersFetcher eligibleInterviewersFetcher;
	private final InterviewUtil interviewUtil;
	private final ExpertPanelManager expertPanelManager;
	private final SchedulingSessionManager schedulingSessionManager;

	@Override
	public String name() {
		return "getEligibleInterviewers";
	}

	@Override
	public String type() {
		return QUERY_TYPE;
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final GetEligibleInterviewersInput input = this.graphQLUtil.getInput(environment,
				GetEligibleInterviewersInput.class);

		final InterviewDAO interviewDAO = this.interViewRepository.findById(input.getInterviewId()).get();
		EligibleInterviewersOutput eligibleInterviewersOutput;

		if (this.interviewUtil.isFastrackedInterview(interviewDAO.getInterviewRound())) {
			eligibleInterviewersOutput = this.handleFasttrackedInterview(interviewDAO);
		} else {
			eligibleInterviewersOutput = this.handleNonFasttrackedInterview(interviewDAO,
					input.getAvailabilityStartDate());
		}
		return DataFetcherResult.newResult()
				.data(eligibleInterviewersOutput)
				.build();
	}

	private EligibleInterviewersOutput handleFasttrackedInterview(final InterviewDAO interviewDAO) {
		final List<Interviewer> interviewers = this
				.fetchInterviewerDetails(this.expertPanelManager.getInterviewPanel(interviewDAO));

		return EligibleInterviewersOutput.builder()
				.interviewers(interviewers)
				.build();
	}

	private EligibleInterviewersOutput handleNonFasttrackedInterview(final InterviewDAO interviewDAO,
			final Long availabilityStartDate)
			throws IOException {
		final InterviewStructureDAO interviewStructureDAO = this.interviewStructureManager.getInterviewStructureById(
				interviewDAO.getInterviewStructureId());
		final Long expertJoiningTime = availabilityStartDate
				+ this.interviewStructureManager.getExpertJoiningTime(interviewStructureDAO);
		final Long endTime = availabilityStartDate
				+ this.interviewStructureManager.getDurationOfInterview(interviewStructureDAO);
		final List<Interviewer> interviewers = this
				.getEligibleExpertsForInterview(interviewDAO, expertJoiningTime, endTime);
		return this.getEligibleInterviewersConsideringAvailability(interviewers, expertJoiningTime,
				endTime);
	}

	private List<Interviewer> getEligibleExpertsForInterview(final InterviewDAO interviewDAO, final Long startDate,
			final Long endDate)
			throws IOException {
		MatchInterviewersData data = this.matchInterviewersDataHelper
				.prepareDataForInterviewSlots(interviewDAO.getId());

		this.eligibleInterviewersFetcher.popluateEligibleInterviewersBasedOnAvailability(data, startDate, endDate);
		this.schedulingSessionManager.storeSchedulingSessionData(interviewDAO.getId(),
				interviewDAO.getRescheduleCount(),
				data.getInterviewCost(),
				data.getBarRaiserUsedMarginPercentage(), data.getBarRaiserConfiguredMarginPercentage());
		return this.fetchInterviewerDetails(data.getInterviewersId());
	}

	private List<Interviewer> fetchInterviewerDetails(final List<String> interviewerIds) {
		final List<UserDetailsDAO> userDetailsDAOS = this.userDetailsRepository.findAllByIdIn(interviewerIds);
		return userDetailsDAOS.stream().map(x -> Interviewer.builder()
				.id(x.getId())
				.build()).collect(Collectors.toList());
	}

	private List<Interviewer> getAvailableInterviewers(final List<Interviewer> interviewers, final Long startTime,
			final Long endTime) {
		final List<String> availableInterviewers = this.availabilityManager
				.getAllAvailableUsers(interviewers.stream().map(Interviewer::getId)
						.collect(Collectors.toList()), startTime, endTime);
		return interviewers.stream().filter(x -> availableInterviewers.contains(x.getId()))
				.collect(Collectors.toList());
	}

	private List<Interviewer> getBookedInterviewers(final List<Interviewer> interviewers,
			final Long startTime, final Long endTime) {
		final List<String> bookedInterviewers = this.availabilityManager.filterForCompletelyBookedExperts(
				interviewers.stream().map(Interviewer::getId).collect(Collectors.toList()), startTime, endTime);
		return interviewers.stream().filter(x -> bookedInterviewers.contains(x.getId()))
				.collect(Collectors.toList());
	}

	private EligibleInterviewersOutput getEligibleInterviewersConsideringAvailability(
			final List<Interviewer> interviewers, final Long startDate, final Long endDate) {

		final List<Interviewer> availableInterviewers = startDate == null ? List.of()
				: this.getAvailableInterviewers(interviewers, startDate, endDate);
		final List<Interviewer> bookedInterviewers = this.getBookedInterviewers(interviewers, startDate,
				endDate);
		final List<Interviewer> unavailableInterviewers = startDate == null ? List.of()
				: ListUtils.subtract(ListUtils.subtract(interviewers, availableInterviewers), bookedInterviewers);
		return EligibleInterviewersOutput.builder().interviewers(interviewers)
				.availableInterviewers(availableInterviewers)
				.unavailableInterviewers(unavailableInterviewers)
				.bookedInterviewers(bookedInterviewers)
				.build();
	}
}
