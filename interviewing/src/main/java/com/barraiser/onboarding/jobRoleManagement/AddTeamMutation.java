/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement;

import com.barraiser.common.graphql.input.TeamInput;
import com.barraiser.common.graphql.types.Team;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.commons.dto.ats.enums.ATSProvider;
import com.barraiser.onboarding.auth.AllowAuthenticatedUserAuthorizationInputConstructor;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.TeamMapper;
import com.barraiser.onboarding.interview.jobrole.dal.TeamDAO;
import com.barraiser.onboarding.interview.jobrole.dal.TeamRepository;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Log4j2
@Component
public class AddTeamMutation extends AuthorizedGraphQLMutation<Team> {

	private final GraphQLUtil graphQLUtil;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final TeamRepository teamRepository;
	private final TeamMapper teamMapper;

	public AddTeamMutation(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			TeamRepository teamRepository,
			TeamMapper teamMapper,
			GraphQLUtil graphQLUtil) {
		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor);
		this.graphQLUtil = graphQLUtil;
		this.authorizationServiceFeignClient = authorizationServiceFeignClient;
		this.teamRepository = teamRepository;
		this.teamMapper = teamMapper;
	}

	@Override
	protected Team fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult)
			throws Exception {
		/* TODO: Add Authorization */
		final TeamInput input = this.graphQLUtil.getInput(environment, TeamInput.class);

		Optional<TeamDAO> existingTeam = this.teamRepository.findByPartnerIdAndName(input.getPartnerId(),
				input.getName());

		if (existingTeam.isEmpty()) {
			TeamDAO team = this.teamRepository.save(
					TeamDAO.builder()
							.id(UUID.randomUUID().toString())
							.name(input.getName())
							.description(input.getDescription())
							.partnerId(input.getPartnerId())
							.build());

			return this.teamMapper.toTeam(team);
		}

		return Team.builder().build();
	}

	@Override
	public String name() {
		return "addTeam";
	}
}
