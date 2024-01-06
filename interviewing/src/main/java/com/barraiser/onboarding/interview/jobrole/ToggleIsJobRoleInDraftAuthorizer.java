/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jobrole;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.UserRole;
import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.GraphQLAbacAuthorizer;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ToggleIsJobRoleInDraftAuthorizer implements GraphQLAbacAuthorizer {
	private final GraphQLUtil graphQLUtil;

	private Boolean isSuperUser(final AuthenticatedUser authenticatedUser) {
		return authenticatedUser.getRoles().contains(UserRole.ADMIN) ||
				authenticatedUser.getRoles().contains(UserRole.OPS) ||
				authenticatedUser.getRoles().contains(UserRole.PARTNER);
	}

	@Override
	public AuthorizationResult authorize(DataFetchingEnvironment environment) {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		if (!this.isSuperUser((authenticatedUser))) {
			throw new AuthorizationException();
		}
		return AuthorizationResult.builder()
				.build();
	}
}
