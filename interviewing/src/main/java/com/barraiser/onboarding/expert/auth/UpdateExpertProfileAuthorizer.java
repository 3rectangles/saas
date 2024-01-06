/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.expert.auth;

import com.barraiser.common.graphql.types.expertProfile.ExpertProfile;
import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.GraphQLAbacAuthorizer;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.commons.auth.UserRole;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class UpdateExpertProfileAuthorizer implements GraphQLAbacAuthorizer {
	private final GraphQLUtil graphQLUtil;

	@Override
	public AuthorizationResult authorize(final DataFetchingEnvironment environment) {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final ExpertProfile input = this.graphQLUtil.getInput(environment, ExpertProfile.class);

		if (!this.isSuperUser(authenticatedUser) && !input.getExpertId().equals(authenticatedUser.getUserName())) {
			throw new AuthorizationException();
		}

		return AuthorizationResult.builder()
				.build();
	}

	private Boolean isSuperUser(final AuthenticatedUser authenticatedUser) {
		return authenticatedUser.getRoles().contains(UserRole.ADMIN) ||
				authenticatedUser.getRoles().contains(UserRole.OPS);
	}
}
