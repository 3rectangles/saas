/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability;

import com.barraiser.common.graphql.input.availability.UpdateRecurringAvailabilityInput;
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
public class UpdateRecurringAvailabilityAuthorizer implements GraphQLAbacAuthorizer {
	private final GraphQLUtil graphQLUtil;

	@Override
	public AuthorizationResult authorize(DataFetchingEnvironment environment) {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final UpdateRecurringAvailabilityInput input = this.graphQLUtil.getInput(environment,
				UpdateRecurringAvailabilityInput.class);

		if (!authenticatedUser.getUserName().equals(input.getUserId())) {
			if (!authenticatedUser.getRoles().contains(UserRole.ADMIN) &&
					!authenticatedUser.getRoles().contains(UserRole.OPS) &&
					!authenticatedUser.getRoles().contains(UserRole.SUPER_ADMIN)) {
				throw new AuthorizationException("User is not authorized to do this operation");
			}
		}
		return AuthorizationResult.builder().build();
	}
}
