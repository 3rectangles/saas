/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations;

import com.barraiser.ats_integrations.dal.ATSUserRoleMappingRepository;
import com.barraiser.common.graphql.input.UpdateATSUserRoleMappingInput;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.commons.dto.User.role.UpdateUserRoleMappingInput;
import com.barraiser.onboarding.auth.AllowAllAuthorizationInputConstructor;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Class is used to update the ats user role to barraiser user role.
 * Of particular use on the UI on the integrations page settings , where a
 * partner company representatives
 * maps the respective roles of their ats appropriately so the synced users from
 * the ATS in future
 * sync schedules get approrpiate roles in BR system
 */

// TODO : Authorization
@Log4j2
@Component
public class UpdateATSUserRoleMapping extends AuthorizedGraphQLMutation<Boolean> {
	private final GraphQLUtil graphQLUtil;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final ATSUserRoleMappingRepository atsUserRoleMappingRepository;
	private final ATSServiceClient atsServiceClient;
	private final ObjectMapper objectMapper;

	public UpdateATSUserRoleMapping(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAllAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			ATSUserRoleMappingRepository atsUserRoleMappingRepository,
			GraphQLUtil graphQLUtil,
			ATSServiceClient atsServiceClient,
			ObjectMapper objectMapper) {

		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor);
		this.graphQLUtil = graphQLUtil;
		this.authorizationServiceFeignClient = authorizationServiceFeignClient;
		this.atsUserRoleMappingRepository = atsUserRoleMappingRepository;
		this.atsServiceClient = atsServiceClient;
		this.objectMapper = objectMapper;
	}

	@Override
	protected Boolean fetch(DataFetchingEnvironment environment,
			AuthorizationResult authorizationResult) {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final UpdateATSUserRoleMappingInput input = this.graphQLUtil.getInput(environment,
				UpdateATSUserRoleMappingInput.class);

		this.updateUserRoleMappings(input);

		return Boolean.TRUE;
	}

	private void updateUserRoleMappings(final UpdateATSUserRoleMappingInput input) {

		this.atsServiceClient.updateUserRoleMappings(
				UpdateUserRoleMappingInput.builder()
						.partnerId(input.getPartnerId())
						.atsProvider(input.getAtsProvider())
						.userRoleMappings(input.getRoleMappings().stream()
								.map(x -> UpdateUserRoleMappingInput.UserRoleMapping.builder()
										.atsUserRoleId(x.getAtsUserRoleId())
										.atsUserRoleName(x.getAtsUserRoleName())
										.brUserRoleId(x.getBrUserRoleId())
										.build())
								.collect(Collectors.toList()))
						.build());

	}

	@Override
	public String name() {
		return "updateATSUserRoleMapping";
	}
}
