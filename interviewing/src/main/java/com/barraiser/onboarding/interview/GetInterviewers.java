/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.input.GetInterviewersInput;
import com.barraiser.common.graphql.types.Interviewer;
import com.barraiser.common.monitoring.Profiled;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.onboarding.interview.interviewer.InterviewerMapper;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import com.barraiser.onboarding.interview.search.InterviewerMatcher;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.UserBlacklistManager;
import graphql.GraphQLContext;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.barraiser.onboarding.common.Constants.ROUND_TYPE_INTERNAL;

@Log4j2
@Component
@AllArgsConstructor
public class GetInterviewers implements NamedDataFetcher<DataFetcherResult<Object>> {
	public static final String DOMAIN = "domain";
	public static final String INTERVIEW_ROUND = "interviewRound";
	public static final String TARGET_COMPANY = "targetCompany";
	public static final String JIRA_INTERVIEW_CANCELLED_KEY = "jira_interview_round_cancelled_status";
	private final String RESPONSE_ERROR = "Either this page does not exists or you do not have the permissions to view this page";
	private final InterviewerMatcher interviewerMatcher;
	private final GraphQLUtil graphQLUtil;
	private final InterViewRepository interviewRepository;
	private final EvaluationRepository evaluationRepository;
	private final DynamicAppConfigProperties dynamicAppConfigProperties;
	private final InterviewStructureRepository interviewStructureRepository;
	private final UserBlacklistManager userBlacklistManager;
	private final JobRoleManager jobRoleManager;
	private final UserDetailsRepository userDetailsRepository;
	private final ExpertRepository expertRepository;
	private final PartnerConfigManager partnerConfigManager;
	private final InterviewerMapper interviewerMapper;

	@Profiled(name = "getInterviewers")
	@Override
	public DataFetcherResult<Object> get(final DataFetchingEnvironment environment) throws Exception {

		/**
		 * It's a three step process:
		 * 1. Search for interviewers in ES
		 * 2. Get Details from database to populate the results.
		 * 3. Attach additional data of Get availability of the interviewers for a given
		 * start and end date.
		 */
		final GetInterviewersInput input = this.graphQLUtil.getArgument(environment, "input",
				GetInterviewersInput.class);
		if (input.getInterviewId() == null) {
			throw new IllegalArgumentException(this.RESPONSE_ERROR);
		}
		final Optional<InterviewDAO> interview = this.interviewRepository.findById(input.getInterviewId());
		if (interview.isEmpty()) {
			throw new IllegalArgumentException(this.RESPONSE_ERROR);
		}
		final String evaluationId = interview.get().getEvaluationId() != null ? interview.get().getEvaluationId() : "";
		final Optional<EvaluationDAO> evaluationDAO = this.evaluationRepository.findById(evaluationId);

		final String jobRoleId = evaluationDAO.isPresent() && evaluationDAO.get().getJobRoleId() != null
				? evaluationDAO.get().getJobRoleId()
				: "";
		final Integer jobRoleVersion = evaluationDAO.isPresent() && evaluationDAO.get().getJobRoleVersion() != null
				? evaluationDAO.get().getJobRoleVersion()
				: 0;
		final Optional<JobRoleDAO> jobRoleDAO = this.jobRoleManager.getJobRole(jobRoleId, jobRoleVersion);

		final String domainId = this.getDomainOfInterview(interview);
		final Map<String, String> params = new HashMap<String, String>() {
			{
				this.put(DOMAIN, domainId);
				this.put(INTERVIEW_ROUND, interview.get().getInterviewRound());
				this.put(TARGET_COMPANY, jobRoleDAO.get().getCompanyId());
			}
		};

		final List<UserDetailsDAO> userDetailsOfEligibleExperts = this
				.getDetailsOfAllEligibleExpertsForInterview(interview.get(), jobRoleDAO.get().getCompanyId(), params);
		final List<ExpertDAO> expertSpecificDetailsOfEligibleExperts = this.expertRepository.findAllByIdIn(
				userDetailsOfEligibleExperts.stream().map(UserDetailsDAO::getId).collect(Collectors.toList()));
		final List<Interviewer> userDetails = userDetailsOfEligibleExperts.stream()
				.map(x -> this.interviewerMapper.toInterviewer(x,
						expertSpecificDetailsOfEligibleExperts.stream().filter(y -> x.getId().equals(y.getId()))
								.findFirst().get()))
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		final GraphQLContext context = environment.getContext();
		if (input.getAvailabilityStartDate() != null) {
			context.put("startDate", input.getAvailabilityStartDate());
		}
		if (input.getAvailabilityEndDate() != null) {
			context.put("endDate", input.getAvailabilityEndDate());
		}

		return DataFetcherResult.newResult()
				.data(userDetails)
				.build();
	}

	public List<String> getInterviewersInSortedOrder(List<UserDetailsDAO> userDetailsDAOList) {
		List<UserDetailsDAO> sortedUserList = userDetailsDAOList.stream()
				.sorted(Comparator.comparing(UserDetailsDAO::getFirstName,
						Comparator.nullsLast(Comparator.naturalOrder())))
				.collect(Collectors.toList());

		return sortedUserList.stream().map(UserDetailsDAO::getId).collect(Collectors.toList());
	}

	public List<String> filterOutBlacklistedInterviewers(final List<String> interviewers, final String companyId) {

		final List<String> blacklistedInterviewers = this.userBlacklistManager
				.getAllBlacklistedInterviewersForCompany(companyId);
		final List<String> nonBlacklistedInterviewers = interviewers.stream()
				.filter(i -> !blacklistedInterviewers.contains(i)).collect(Collectors.toList());
		return nonBlacklistedInterviewers;
	}

	public List<String> getAllUnusedInterviewers(final String evaluationId, final List<String> interviewers) {

		final List<String> usedInterviewers = this.interviewRepository.findAllByEvaluationId(evaluationId).stream()
				.filter(x -> !x.getStatus()
						.equalsIgnoreCase(this.dynamicAppConfigProperties.getString(JIRA_INTERVIEW_CANCELLED_KEY)))
				.map(InterviewDAO::getInterviewerId).collect(Collectors.toList());

		return interviewers.stream()
				.filter(x -> !usedInterviewers.contains(x))
				.collect(Collectors.toList());
	}

	public String getDomainOfInterview(final Optional<InterviewDAO> interview) {
		final String interviewStructureId = interview.get().getInterviewStructureId();
		final Optional<InterviewStructureDAO> interviewStructureDAO = this.interviewStructureRepository
				.findById(interviewStructureId);
		final String domainId = interviewStructureDAO.isPresent() ? interviewStructureDAO.get().getDomainId() : "";
		return domainId;
	}

	private List<UserDetailsDAO> getDetailsOfAllEligibleExpertsForInterview(final InterviewDAO interview,
			final String companyId, final Map<String, String> params) throws IOException {
		List<String> allInterviewers = this.interviewerMatcher.getInterviewers(params);
		List<UserDetailsDAO> userDetailsOfExpert = this.userDetailsRepository.findAllByIdIn(allInterviewers);
		List<String> interviewers = ROUND_TYPE_INTERNAL.equalsIgnoreCase(interview.getInterviewRound())
				? this.getInterviewersInSortedOrder(userDetailsOfExpert)
				: this.getAllUnusedInterviewers(interview.getEvaluationId(),
						allInterviewers);

		// Removes the experts that are blacklisted
		final String partnerCompanyId = this.partnerConfigManager.getPartnerIdFromCompanyId(companyId);
		final List<String> eligibleExperts = this.filterOutBlacklistedInterviewers(interviewers, partnerCompanyId);
		log.info("interviewers {}", eligibleExperts.size());

		return userDetailsOfExpert.stream().filter(x -> eligibleExperts.contains(x.getId()))
				.collect(Collectors.toList());
	}

	@Override
	public String name() {
		return "getInterviewers";
	}

	@Override
	public String type() {
		return QUERY_TYPE;
	}
}
