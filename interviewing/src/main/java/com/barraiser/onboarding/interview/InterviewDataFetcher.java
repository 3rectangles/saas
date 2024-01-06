/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.enums.MeetingPlatform;
import com.barraiser.common.graphql.input.GetInterviewsInput;
import com.barraiser.common.graphql.types.CandidateInterviewFeedback;
import com.barraiser.common.graphql.types.Evaluation;
import com.barraiser.common.graphql.types.Interview;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.auth.AuthorizationResourceDTO;
import com.barraiser.onboarding.auth.Authorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.specifications.InterviewSpecifications;
import com.barraiser.onboarding.graphql.Constants;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.MultiParentTypeDataFetcher;
import com.barraiser.onboarding.interview.auth.InterviewAuthorizer;
import com.barraiser.onboarding.interviewing.meeting.InterviewMeetingUtils;
import com.barraiser.onboarding.user.TimezoneManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.antlr.v4.runtime.misc.Pair;
import org.apache.commons.collections.map.HashedMap;
import org.dataloader.DataLoader;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewDataFetcher implements MultiParentTypeDataFetcher {

	private static final String TYPE_EVALUATION = "Evaluation";
	private static final String INTERVIEW_STATUS_FILTER_ALL = "ALL_INTERVIEWS";
	// default interviews corresponds to filter of status which was present already
	// for backward compatibility
	private static final String INTERVIEW_STATUS_FILTER_DEFAULT = "DEFAULT_INTERVIEWS";
	private static final String INTERVIEW_STATUS_FILTER_VALID_INTERVIEWS = "VALID_INTERVIEWS";
	private final InterViewRepository interViewRepository;
	private final JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	private final InterviewSpecifications interviewSpecifications;
	private final EvaluationRepository evaluationRepository;
	private final JiraUUIDRepository jiraUUIDRepository;
	private final GraphQLUtil graphQLUtil;
	private final ObjectMapper objectMapper;
	private final InterviewMapper interviewMapper;
	private final InterviewStructureRepository interviewStructureRepository;
	private final InterviewUtil interviewUtil;
	private final ExpertInterviewsFetcher expertInterviewsFetcher;
	private final TimezoneManager timezoneManager;
	private final Authorizer authorizer;
	private final InterviewMeetingUtils interviewMeetingUtils;

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(QUERY_TYPE, Constants.QUERY_GET_INTERVIEWS),
				List.of(TYPE_EVALUATION, "interviews"),
				List.of(TYPE_EVALUATION, "allInterviews"),
				List.of(TYPE_EVALUATION, "validInterviews"),
				List.of("CandidateInterviewFeedback", "interview"));
	}

	public static final String INTERVIEW_FOR_EVALUATIONS_DATA_LOADER = "INTERVIEW_FOR_EVALUATIONS_DATA_LOADER";
	public static final String INTERVIEWS_FOR_INTERVIEW_IDS_DATA_LOADER = "INTERVIEWS_FOR_INTERVIEW_IDS_DATA_LOADER";

	private static final Executor executor = Executors
			.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	public DataLoader<Pair<String, Evaluation>, List<Interview>> createInterviewForEvaluationsDataLoader() {
		return DataLoader.newMappedDataLoader(
				(Set<Pair<String, Evaluation>> evaluationSet) -> CompletableFuture.supplyAsync(() -> {
					final Map<Pair<String, Evaluation>, List<Interview>> interviewListMap = new HashedMap();
					for (final Pair<String, Evaluation> evaluation : evaluationSet) {
						interviewListMap.put(evaluation, new ArrayList<>());
					}
					final List<String> validDefaultStatusForCreatingEvaluation = List.of(
							InterviewStatus.PENDING_QC.getValue(),
							InterviewStatus.PENDING_CORRECTION.getValue(),
							InterviewStatus.DONE.getValue());
					// TODO: use a different Pair implementation
					final List<Evaluation> evaluations = evaluationSet.stream()
							.map(x -> x.b)
							.distinct()
							.collect(Collectors.toList());

					final List<InterviewDAO> interviewDAOS = this.interViewRepository.findAllByEvaluationIdIn(
							evaluations.stream().map(Evaluation::getId).collect(Collectors.toList()));
					final List<Interview> interviews = this.mapInterviewDAOListToInterviewList(interviewDAOS);
					for (final Pair<String, Evaluation> evaluation : evaluationSet) {
						if (evaluation.a.equals(INTERVIEW_STATUS_FILTER_ALL)) {
							interviewListMap.put(evaluation, this.getAllInterviews(interviews.stream()
									.filter(i -> evaluation.b.getId().equals(i.getEvaluationId()))
									.map(i -> i.toBuilder().scoringAlgoVersion(evaluation.b.getScoringAlgoVersion())
											.build())
									.collect(Collectors.toList())));
						} else if (evaluation.a.equals(INTERVIEW_STATUS_FILTER_VALID_INTERVIEWS)) {
							interviewListMap.put(evaluation, this.getValidInterviews(interviews.stream()
									.filter(i -> evaluation.b.getId().equals(i.getEvaluationId()))
									.map(i -> i.toBuilder().scoringAlgoVersion(evaluation.b.getScoringAlgoVersion())
											.build())
									.collect(Collectors.toList())));
						} else {
							interviewListMap.put(evaluation, interviews.stream()
									.filter(i -> evaluation.b.getId().equals(i.getEvaluationId()) &&
											validDefaultStatusForCreatingEvaluation.contains(i.getStatus()) &&
											!Boolean.FALSE.equals(i.getIsRedoEligible()))
									.collect(Collectors.toList()));
						}
						this.sortInterviews(interviewListMap.get(evaluation));
						interviewListMap.put(evaluation, this.fillRoundNumbers(interviewListMap.get(evaluation)));
					}
					return interviewListMap;
				}, executor));
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {

		final GetInterviewsInput input = this.graphQLUtil.getArgument(environment, "input", GetInterviewsInput.class);

		final GraphQLObjectType type = (GraphQLObjectType) environment.getParentType();
		log.info("Parent type : {}", type);

		final List<InterviewDAO> interviews;
		if (type.getName().equals(QUERY_TYPE)) {
			// trying to get the id
			String interviewId = input.getInterviewId();
			if (input.getJira() != null) {
				interviewId = this.jiraUUIDRepository.findById(input.getJira())
						.orElse(JiraUUIDDAO.builder().build()).getUuid();

				if (interviewId == null) {
					throw new IllegalArgumentException("No interview is mapped to JIRA");
				}
			}
			log.info("getInterviews for id : {}", interviewId);
			if (input.getStartingTime() == null && input.getEndingTime() == null) {
				final Specification<InterviewDAO> specifications = this.interviewSpecifications
						.getInterviewDAOSpecification(input, interviewId);
				interviews = this.interViewRepository.findAll(specifications);
			} else if (input.getEndingTime() == null) {
				final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(environment);
				this.authorizer.can(user, InterviewAuthorizer.ACTION_READ, AuthorizationResourceDTO.builder()
						.type(InterviewAuthorizer.RESOURCE_TYPE)
						.resource(input)
						.build());

				final Specification<InterviewDAO> specifications = this.interviewSpecifications
						.getUpcomingInterviewsSpecification(input.getPartnerId(), input.getInterviewerId(),
								input.getStartingTime(),
								input.getExcludedStatuses());
				interviews = this.interViewRepository.findAll(specifications);
			} else {
				// Used for interview dashboard , my-interviews tab

				final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(environment);
				this.authorizer.can(user, InterviewAuthorizer.ACTION_READ, AuthorizationResourceDTO.builder()
						.type(InterviewAuthorizer.RESOURCE_TYPE)
						.resource(input)
						.build());

				interviews = this.expertInterviewsFetcher.getInterviewsForExpert(
						input.getPartnerId(),
						input.getInterviewerId(),
						input.getStartingTime(),
						input.getEndingTime(), input.getIncludedStatuses(), input.getExcludedStatuses());
			}

		} else if (type.getName().equals(TYPE_EVALUATION)) {
			final DataLoader<Pair<String, Evaluation>, List<Interview>> interviewForEvaluationsDataLoader = environment
					.getDataLoader(INTERVIEW_FOR_EVALUATIONS_DATA_LOADER);
			final Evaluation evaluation = environment.getSource();
			return interviewForEvaluationsDataLoader.load(new Pair<>(
					environment.getFieldDefinition().getName().equals("allInterviews") ? INTERVIEW_STATUS_FILTER_ALL
							: environment.getFieldDefinition().getName().equals("validInterviews")
									? INTERVIEW_STATUS_FILTER_VALID_INTERVIEWS
									: INTERVIEW_STATUS_FILTER_DEFAULT,
					evaluation));
		} else if (type.getName().equals("CandidateInterviewFeedback")) {
			final DataLoader<String, List<Interview>> interviewsForInterviewListDataLoader = environment
					.getDataLoader(INTERVIEWS_FOR_INTERVIEW_IDS_DATA_LOADER);
			final CandidateInterviewFeedback interviewFeedback = environment.getSource();
			return interviewsForInterviewListDataLoader.load(interviewFeedback.getInterviewId());
		} else {
			throw new IllegalArgumentException("Bad parent type while accessing Interview type, please fix your query");
		}

		interviews.sort(Comparator.comparing(InterviewDAO::getStartDate).reversed());

		log.info(interviews.size());

		final List<Interview> interviewsResponseList = this.mapInterviewDAOListToInterviewList(interviews);

		return DataFetcherResult.newResult()
				.data(interviewsResponseList)
				.build();
	}

	private List<Interview> mapInterviewDAOListToInterviewList(final List<InterviewDAO> interviews) {
		final Set<String> evaluationIds = interviews.stream().map(interview -> interview.getEvaluationId())
				.collect(Collectors.toSet());
		final List<EvaluationDAO> evaluationDAOS = this.evaluationRepository.findAllById(evaluationIds);
		final Map<String, EvaluationDAO> evaluationDAOMap = new HashedMap();
		for (final EvaluationDAO evaluationDAO : evaluationDAOS) {
			evaluationDAOMap.put(evaluationDAO.getId(), evaluationDAO);
		}
		final List<Pair<String, Integer>> jobRolePair = evaluationDAOS.stream()
				.map(e -> new Pair<String, Integer>(e.getJobRoleId(), e.getJobRoleVersion()))
				.collect(Collectors.toList());
		final List<String> jobRoleIdVersionConcatedList = jobRolePair.stream().map(p -> p.a + p.b)
				.collect(Collectors.toList());
		final List<JobRoleToInterviewStructureDAO> jobRoleToInterviewStructureDAOList = this.jobRoleToInterviewStructureRepository
				.findAllByJobRoleIdJobRoleVersionIn(jobRoleIdVersionConcatedList);
		final Map<Pair<String, Integer>, List<JobRoleToInterviewStructureDAO>> jobRoleToInterviewStructureMap = new HashedMap();
		for (final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO : jobRoleToInterviewStructureDAOList) {
			final Pair<String, Integer> pairId = new Pair<String, Integer>(
					jobRoleToInterviewStructureDAO.getJobRoleId(), jobRoleToInterviewStructureDAO.getJobRoleVersion());
			final List<JobRoleToInterviewStructureDAO> jobRoleToInterviewStructureDAOS = jobRoleToInterviewStructureMap
					.getOrDefault(pairId, new ArrayList<>());
			jobRoleToInterviewStructureDAOS.add(jobRoleToInterviewStructureDAO);
			jobRoleToInterviewStructureMap.put(pairId, jobRoleToInterviewStructureDAOS);
		}
		final List<String> interviewStructureIds = jobRoleToInterviewStructureDAOList.stream()
				.map(JobRoleToInterviewStructureDAO::getInterviewStructureId).collect(Collectors.toList());
		final List<InterviewStructureDAO> interviewStructureDAOS = this.interviewStructureRepository
				.findAllByIdIn(interviewStructureIds);

		return interviews.stream().map(x -> {
			final Interview interview = this.objectMapper.convertValue(x, Interview.class);

			final String jobRoleId = interview.getEvaluationId() == null ? null
					: evaluationDAOMap.containsKey(interview.getEvaluationId())
							? evaluationDAOMap.get(interview.getEvaluationId()).getJobRoleId()
							: null;
			final Integer jobRoleVersion = interview.getEvaluationId() == null ? null
					: evaluationDAOMap.containsKey(interview.getEvaluationId())
							? evaluationDAOMap.get(interview.getEvaluationId()).getJobRoleVersion()
							: null;

			final String scoringAlgoVersion = interview.getEvaluationId() == null ? null
					: evaluationDAOMap.containsKey(interview.getEvaluationId())
							? evaluationDAOMap.get(interview.getEvaluationId()).getDefaultScoringAlgoVersion()
							: null;
			final Pair<String, Integer> pairId = new Pair<String, Integer>(jobRoleId, jobRoleVersion);
			JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO = null;
			for (final JobRoleToInterviewStructureDAO j : jobRoleToInterviewStructureMap.getOrDefault(pairId,
					List.of())) {
				if (j.getInterviewStructureId().equals(interview.getInterviewStructureId())) {
					jobRoleToInterviewStructureDAO = j;
				}
			}

			final InterviewStructureDAO interviewStructureDAO = interviewStructureDAOS.stream()
					.filter(i -> i.getId().equals(x.getInterviewStructureId())).findFirst().orElse(null);
			final String interviewStructureLink = jobRoleToInterviewStructureDAO != null
					? jobRoleToInterviewStructureDAO.getInterviewStructureLink()
					: null;

			Long duration = null;
			if (x.getDuration() != null) {
				duration = x.getDuration().longValue();
			} else if (interviewStructureDAO != null && interviewStructureDAO.getDuration() != null) {
				duration = interviewStructureDAO.getDuration().longValue();
			}

			final Long expertJoiningTime = (interviewStructureDAO != null
					&& interviewStructureDAO.getExpertJoiningTime() != null)
							? interviewStructureDAO.getExpertJoiningTime().longValue()
							: 0;

			final Integer orderIndex = jobRoleToInterviewStructureDAO != null
					? jobRoleToInterviewStructureDAO.getOrderIndex()
					: null;

			final Integer roundNumber = this.interviewUtil.getRoundNumberOfInterview(x);

			return interview.toBuilder()
					.jobRoleId(jobRoleId)
					.jobRoleVersion(jobRoleVersion)
					.scheduledStartDate(x.getStartDate())
					.expertScheduledStartDate(x.getStartDate() == null ? null : x.getStartDate() + expertJoiningTime)
					.scheduledEndDate(x.getEndDate())
					.startDate(x.getInterviewStartTime())
					.videoStartTime(x.getVideoStartTime())
					.endDate(x.getActualEndDate())
					.interviewStructureLink(interviewStructureLink)
					.submittedCodeLink(x.getSubmittedCodeLink())
					.createdOn(x.getCreatedOn())
					.durationInMinutes(duration)
					.scoringAlgoVersion(scoringAlgoVersion)
					.orderIndex(orderIndex)
					.roundNumber(roundNumber)
					.requiresApproval(jobRoleToInterviewStructureDAO != null && x.getInterviewStructureId() != null
							&& this.interviewUtil.doesInterviewRequireApproval(x, jobRoleToInterviewStructureDAO))
					.intervieweeTimezone(this.timezoneManager.getTimezoneOfCandidate(x.getId()))
					.isRedoEligible(this.interviewUtil.shouldInterviewBeConsideredForEvaluation(x))
					.meetingPlatform(
							x.getMeetingLink() != null ? MeetingPlatform.valueOf(
									this.interviewMeetingUtils.getMeetingPlatformFromURL(
											x.getMeetingLink()).getValue())
									: null)
					.atsInterviewFeedbackLink(x.getAtsInterviewFeedbackLink())
					.build();
		}).collect(Collectors.toList());
	}

	public List<Interview> getValidInterviews(final List<Interview> interviews) {

		final List<Interview> validInterviews = new ArrayList<>();

		final Map<String, Interview> interviewStructureToInterview = new HashMap<>();
		for (final Interview interview : interviews) {
			if (interviewStructureToInterview.containsKey(interview.getInterviewStructureId())) {
				if (interview.getCreatedOn().compareTo(
						interviewStructureToInterview.get(interview.getInterviewStructureId()).getCreatedOn()) > 0) {
					interviewStructureToInterview.replace(interview.getInterviewStructureId(), interview);
				}
			} else {
				interviewStructureToInterview.put(interview.getInterviewStructureId(), interview);
			}
		}

		interviewStructureToInterview.forEach((id, interview) -> validInterviews.add(interview));
		return validInterviews;
	}

	public void sortInterviews(final List<Interview> interviews) {
		interviews.sort((i1, i2) -> {
			if (i1.getOrderIndex() == null)
				return 1;
			if (i2.getOrderIndex() == null)
				return -1;

			if (i1.getOrderIndex() == i2.getOrderIndex() && i1.getScheduledStartDate() != null
					&& i2.getScheduledStartDate() != null) {
				if (i1.getScheduledStartDate() > i2.getScheduledStartDate()) {
					return 1;
				}
				if (i1.getScheduledStartDate() < i2.getScheduledStartDate()) {
					return -1;
				}
			}

			return i1.getOrderIndex() - i2.getOrderIndex();
		});
	}

	private List<Interview> fillRoundNumbers(final List<Interview> interviews) {
		final List<Interview> updatedInterviews = new ArrayList<>();
		for (int i = 0; i < interviews.size(); ++i) {
			updatedInterviews.add(interviews.get(i).toBuilder().roundNumber(i + 1).build());
		}
		return updatedInterviews;
	}

	public DataLoader<String, Interview> getInterviewsForInterviewIdsDataLoader() {
		return DataLoader.newMappedDataLoader((Set<String> interviewIdsSet) -> CompletableFuture.supplyAsync(() -> {
			Map<String, Interview> interviewsListMap = new HashMap<>();
			List<InterviewDAO> result = this.interViewRepository.findAllByIdIn(new ArrayList<>(interviewIdsSet));
			result.forEach(x -> interviewsListMap.put(x.getId(), this.interviewMapper.toInterview(x)));
			return interviewsListMap;
		}, executor));
	}

	private List<Interview> getAllInterviews(final List<Interview> interviews) {
		final List<Interview> allInterviews = new ArrayList<>();

		final Map<String, Interview> interviewStructureToInterview = new HashMap<>();
		for (final Interview interview : interviews) {
			if (interviewStructureToInterview.containsKey(interview.getInterviewStructureId())) {
				if (interview.getCreatedOn().compareTo(
						interviewStructureToInterview.get(interview.getInterviewStructureId()).getCreatedOn()) > 0) {
					interviewStructureToInterview.replace(interview.getInterviewStructureId(), interview);
				}
			} else {
				interviewStructureToInterview.put(interview.getInterviewStructureId(), interview);
			}

			if (Boolean.FALSE.equals(interview.getIsRedoEligible())) {
				allInterviews.add(interview);
			}
		}

		interviewStructureToInterview.forEach((id, interview) -> allInterviews.add(interview));
		return allInterviews;
	}
}
