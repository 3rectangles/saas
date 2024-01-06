/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.input.GetTeamsInput;
import com.barraiser.common.graphql.types.Team;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.auth.AllowAuthenticatedUserAuthorizationInputConstructor;
import com.barraiser.onboarding.dal.JobRoleRepository;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.jobrole.dal.TeamRepository;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.barraiser.onboarding.graphql.NamedDataFetcher.QUERY_TYPE;

@Component
@Log4j2
public class TeamDataFetcher extends AuthorizedGraphQLQuery<List<Team>> {
	private final GraphQLUtil graphQLUtil;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final JobRoleRepository jobRoleRepository;
	private final TeamRepository teamRepository;
	private final TeamMapper teamMapper;

	public TeamDataFetcher(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			ObjectFieldsFilter objectFieldsFilter,
			JobRoleRepository jobRoleRepository,
			TeamRepository teamRepository,
			TeamMapper teamMapper,
			GraphQLUtil graphQLUtil) {
		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor, objectFieldsFilter);
		this.graphQLUtil = graphQLUtil;
		this.authorizationServiceFeignClient = authorizationServiceFeignClient;
		this.jobRoleRepository = jobRoleRepository;
		this.teamRepository = teamRepository;
		this.teamMapper = teamMapper;
	}

	@Override
	protected List<Team> fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult) {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);

		final GetTeamsInput input = this.graphQLUtil.getInput(environment, GetTeamsInput.class);

		return this.teamRepository.findAllByPartnerId(input.getPartnerId())
				.stream()
				.map(this.teamMapper::toTeam)
				.collect(Collectors.toList());
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(QUERY_TYPE, "fetchTeams"));
	}
}
