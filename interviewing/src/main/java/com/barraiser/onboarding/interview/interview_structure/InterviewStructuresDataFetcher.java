/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.interview_structure;

import com.barraiser.common.graphql.input.GetInterviewStructureInput;
import com.barraiser.common.graphql.types.InterviewStructure;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.onboarding.dal.InterviewStructureRepository;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery_deprecated;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InterviewStructuresDataFetcher extends AuthorizedGraphQLQuery_deprecated<List<InterviewStructure>> {
	private final InterviewStructureRepository interviewStructureRepository;
	private final GraphQLUtil graphQLUtil;
	private final ObjectMapper objectMapper;

	public InterviewStructuresDataFetcher(final InterviewStructureAuthorizer abacAuthorizer,
			final ObjectFieldsFilter<List<InterviewStructure>> objectFieldsFilter,
			final InterviewStructureRepository interviewStructureRepository,
			final GraphQLUtil graphQLUtil, ObjectMapper objectMapper) {
		super(abacAuthorizer, objectFieldsFilter);
		this.interviewStructureRepository = interviewStructureRepository;
		this.graphQLUtil = graphQLUtil;
		this.objectMapper = objectMapper;
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of("Query", "getInterviewStructure"));
	}

	@Override
	public List<InterviewStructure> fetch(final DataFetchingEnvironment environment,
			final AuthorizationResult authorizationResult) {
		final GraphQLObjectType type = (GraphQLObjectType) environment.getParentType();

		if (type.getName().equals("Query")) {
			return this.getInterviewStructureForQuery(environment);

		} else {
			throw new IllegalArgumentException(
					"Bad parent type while accessing interview structure type, please fix your query");
		}

	}

	private List<InterviewStructure> getInterviewStructureForQuery(DataFetchingEnvironment environment) {
		final GetInterviewStructureInput getInterviewStructureInput = this.graphQLUtil.getArgument(environment,
				"input", GetInterviewStructureInput.class);
		final String id = getInterviewStructureInput.getInterviewStructureId();

		if (id != null) {

			return Collections.singletonList(this.objectMapper.convertValue(
					this.interviewStructureRepository.findById(id), InterviewStructure.class));

		} else {
			return this.interviewStructureRepository.findAll().stream()
					.map(interviewStructureDAO -> this.objectMapper.convertValue(interviewStructureDAO,
							InterviewStructure.class))
					.collect(Collectors.toList());
		}
	}
}
