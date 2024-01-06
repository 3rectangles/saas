/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.Evaluation;
import com.barraiser.common.graphql.types.Interview;
import com.barraiser.common.graphql.types.Interviewee;
import com.barraiser.onboarding.auth.magicLink.MagicLinkManager;
import com.barraiser.onboarding.candidate.CandidateInformationManager;
import com.barraiser.onboarding.dal.CandidateDAO;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.MultiParentTypeDataFetcher;
import com.barraiser.onboarding.user.candidate.GetIntervieweeInput;
import com.barraiser.onboarding.user.candidate.mapper.IntervieweeMapper;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.dataloader.DataLoader;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@Log4j2
public class IntervieweeDataFetcher implements MultiParentTypeDataFetcher {
	private final CandidateInformationManager candidateInformationManager;
	private final UserDetailsRepository userDetailsRepository;
	private final GraphQLUtil graphQLUtil;
	private final IntervieweeMapper intervieweeMapper;
	private final MagicLinkManager magicLinkManager;

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(QUERY_TYPE, "getInterviewee"),
				List.of("Interview", "interviewee"),
				List.of("Evaluation", "candidate"));
	}

	public static final String INTERVIEWEE_DATA_LOADER = "INTERVIEWEE_DATA_LOADER";

	private static final Executor executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	private static Long interviewEndDate;

	public DataLoader<String, Interviewee> createIntervieweeDataLoader() {
		return DataLoader.newMappedDataLoader(
				(Set<String> ids) -> CompletableFuture.supplyAsync(
						() -> {
							final Map<String, Interviewee> userDetailsDAOMap = this.getIdToIntervieweeMapping(ids);
							return userDetailsDAOMap;
						},
						executor));
	}

	private Map<String, Interviewee> getIdToIntervieweeMapping(Set<String> ids) {
		final Map<String, Interviewee> intervieweeDetailsMap = new HashMap<>();
		final List<CandidateDAO> candidateDAOs = this.candidateInformationManager
				.getCandidates(ids);
		final Map<String, UserDetailsDAO> userIdToUserMapping = this
				.getIdToUserMapping(candidateDAOs);

		for (final String id : ids) {
			intervieweeDetailsMap.put(id, null);
		}

		for (final CandidateDAO candidateDAO : candidateDAOs) {
			UserDetailsDAO userDetailDAO = candidateDAO.getUserId() != null
					? userIdToUserMapping.get(candidateDAO.getUserId())
					: UserDetailsDAO.builder().build();
			Interviewee interviewee = this.intervieweeMapper.toInterviewee(candidateDAO,
					userDetailDAO);
			if (interviewEndDate != null) {
				String magicToken = this.getMagicToken(interviewee.getEmail(), interviewEndDate);
				interviewee = interviewee.toBuilder().magicToken(magicToken).build();
			}
			intervieweeDetailsMap.put(interviewee.getId(), interviewee);
		}
		return intervieweeDetailsMap;
	}

	private Map<String, UserDetailsDAO> getIdToUserMapping(final List<CandidateDAO> candidateDAOs) {
		final List<String> candidateUserIds = candidateDAOs.stream()
				.map(c -> c.getUserId())
				.filter(Objects::nonNull)
				.collect(Collectors.toList());

		final Map<String, UserDetailsDAO> userIdToUserMapping = this.userDetailsRepository.findAllById(candidateUserIds)
				.stream()
				.collect(Collectors.toMap(UserDetailsDAO::getId, Function.identity()));
		return userIdToUserMapping;
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final GraphQLObjectType type = (GraphQLObjectType) environment.getParentType();
		final String candidateId;

		final DataLoader<String, Interviewee> intervieweeDataLoader = environment
				.getDataLoader(INTERVIEWEE_DATA_LOADER);

		if (type.getName().equals("Interview")) {
			final Interview interview = environment.getSource();
			candidateId = interview.getIntervieweeId();
			interviewEndDate = interview.getScheduledEndDate();
		} else if (type.getName().equals("Evaluation")) {
			final Evaluation evaluation = environment.getSource();
			candidateId = evaluation.getCandidateId();
		} else if (type.getName().equals("Query")) {
			final GetIntervieweeInput input = this.graphQLUtil.getInput(environment, GetIntervieweeInput.class);
			if (input != null && input.getIntervieweeId() != null) {
				candidateId = input.getIntervieweeId();
			} else {
				candidateId = this.graphQLUtil.getLoggedInUser(environment).getUserName();
			}
			log.info("Candidate {}", candidateId);
		} else {
			throw new IllegalArgumentException(
					"Bad parent type while accessing Interviewee type, please fix your query");
		}

		return intervieweeDataLoader.load(candidateId);
	}

	private String getMagicToken(String candidateEmail, Long interviewEndDate) {

		if (candidateEmail == null) {
			return null;
		}

		final Long loginLinkExpiration = interviewEndDate - Instant.now().getEpochSecond();
		return this.magicLinkManager.generateJWT(candidateEmail, loginLinkExpiration);
	}

}
