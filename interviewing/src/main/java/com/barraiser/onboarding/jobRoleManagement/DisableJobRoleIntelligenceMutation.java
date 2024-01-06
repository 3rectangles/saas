/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement;

import com.barraiser.common.dal.VersionedEntityId;
import com.barraiser.common.graphql.input.DisableJobRoleIntelligenceInput;
import com.barraiser.common.graphql.types.JobRole;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.auth.AllowAuthenticatedUserAuthorizationInputConstructor;
import com.barraiser.onboarding.dal.JobRoleDAO;
import com.barraiser.onboarding.dal.JobRoleRepository;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import com.barraiser.onboarding.interview.jobrole.JobRoleMapper;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.barraiser.onboarding.common.Constants.JOBROLE_INTELLIGENCE_DISABLED_STATUS_ID;
import static com.barraiser.onboarding.interview.jobrole.JobRoleCategory.E;

@Log4j2
@Component
public class DisableJobRoleIntelligenceMutation extends AuthorizedGraphQLMutation<JobRole> {

	private final GraphQLUtil graphQLUtil;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final JobRoleRepository jobRoleRepository;
	private final JobRoleMapper jobRoleMapper;

	public DisableJobRoleIntelligenceMutation(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			JobRoleRepository jobRoleRepository,
			JobRoleMapper jobRoleMapper,
			GraphQLUtil graphQLUtil) {
		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor);
		this.graphQLUtil = graphQLUtil;
		this.authorizationServiceFeignClient = authorizationServiceFeignClient;
		this.jobRoleRepository = jobRoleRepository;
		this.jobRoleMapper = jobRoleMapper;
	}

	@Override
	protected JobRole fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult) {
		/* TODO: Add Authorization */

		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final DisableJobRoleIntelligenceInput input = this.graphQLUtil.getInput(environment,
				DisableJobRoleIntelligenceInput.class);

		Optional<JobRoleDAO> jobRoleDAO = this.jobRoleRepository.findByEntityId(
				new VersionedEntityId(
						input.getJobRoleId(), input.getJobRoleVersion()));

		JobRoleDAO savedJobRole = jobRoleRepository.save(jobRoleDAO.get().toBuilder()
				.extFullSync(false)
				.extFullSyncStatus("")
				.brStatus(List.of(JOBROLE_INTELLIGENCE_DISABLED_STATUS_ID))
				.build());

		return this.jobRoleMapper.toJobRole(savedJobRole);
	}

	@Override
	public String name() {
		return "disableJobRoleIntelligence";
	}
}
