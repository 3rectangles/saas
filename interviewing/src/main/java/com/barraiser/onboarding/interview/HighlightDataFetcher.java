/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.Speaker;
import com.barraiser.common.graphql.types.GetHighlightsInput;
import com.barraiser.common.graphql.types.Highlight;
import com.barraiser.common.graphql.types.HighlightQuestion;
import com.barraiser.common.graphql.types.Skill;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.auth.AllowAuthenticatedUserAuthorizationInputConstructor;
import com.barraiser.onboarding.dal.SkillDAO;
import com.barraiser.onboarding.dal.SkillRepository;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.evaluation.dal.*;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.barraiser.onboarding.graphql.NamedDataFetcher.QUERY_TYPE;

@Component
public class HighlightDataFetcher extends AuthorizedGraphQLQuery<List<Highlight>> {

	private final GraphQLUtil graphQLUtil;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final HighlightRepository highlightRepository;
	private final HighlightQuestionRepository highlightQuestionRepository;
	private final SkillRepository skillRepository;

	public HighlightDataFetcher(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			ObjectFieldsFilter objectFieldsFilter,
			HighlightRepository highlightRepository,
			HighlightQuestionRepository highlightQuestionRepository,
			SkillRepository skillRepository,
			GraphQLUtil graphQLUtil) {
		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor, objectFieldsFilter);
		this.graphQLUtil = graphQLUtil;
		this.authorizationServiceFeignClient = authorizationServiceFeignClient;
		this.highlightRepository = highlightRepository;
		this.highlightQuestionRepository = highlightQuestionRepository;
		this.skillRepository = skillRepository;
	}

	@Override
	protected List<Highlight> fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult) {
		/* TODO: Add Authorization */
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final GetHighlightsInput input = this.graphQLUtil.getInput(environment, GetHighlightsInput.class);

		return this.getHighlightsList(input.getInterviewId());
	}

	private List<Highlight> getHighlightsList(final String interviewId) {
		List<Highlight> highlightList = new ArrayList<>();

		for (HighlightDAO highlightDAO : this.highlightRepository.findByInterviewId(interviewId)) {
			highlightList.add(
					Highlight.builder()
							.id(highlightDAO.getId())
							.description(highlightDAO.getDescription())
							.title(highlightDAO.getDescription())
							.startTime(highlightDAO.getStartTime())
							.endTime(highlightDAO.getEndTime())
							.questions(this.getHighlightQuestionsForHighlight(highlightDAO.getId()))
							.skills(this.getSkillsForHighlight(highlightDAO))
							.build());
		}

		return highlightList;
	}

	private List<HighlightQuestion> getHighlightQuestionsForHighlight(final String highlightId) {
		final List<HighlightQuestion> highlightQuestions = new ArrayList<>();

		// Sending highlight questions ordered by offset time.
		for (HighlightQuestionDAO highlightQuestionDAO : this.highlightQuestionRepository
				.findByHighlightIdOrderByOffsetTime(highlightId)) {

			highlightQuestions.add(
					HighlightQuestion.builder()
							.id(highlightQuestionDAO.getId())
							.description(highlightQuestionDAO.getDescription())
							.offsetTime(highlightQuestionDAO.getOffsetTime())
							.speaker(Speaker.builder()
									.name(highlightQuestionDAO.getSpeaker())
									.build())
							.build());
		}

		return highlightQuestions;
	}

	private List<Skill> getSkillsForHighlight(final HighlightDAO highlightDAO) {
		if (highlightDAO.getSkillIds() == null)
			return null;

		return highlightDAO.getSkillIds().stream().map(
				s -> {
					SkillDAO skillDAO = this.skillRepository.findById(s).get();
					return Skill.builder().name(skillDAO.getName())
							.id(skillDAO.getId())
							.build();
				}).collect(Collectors.toList());
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(QUERY_TYPE, "getHighlights"));
	}
}
