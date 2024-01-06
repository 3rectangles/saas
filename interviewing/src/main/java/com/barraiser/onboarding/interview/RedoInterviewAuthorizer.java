/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.GraphQLAbacAuthorizer;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.graphql.input.RedoInterviewInput;
import com.barraiser.onboarding.partner.PartnerRepAuthorizer;
import com.barraiser.commons.auth.UserRole;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RedoInterviewAuthorizer implements GraphQLAbacAuthorizer {
	private final GraphQLUtil graphQLUtil;
	private final PartnerRepAuthorizer partnerRepAuthorizer;

	@Override
	public AuthorizationResult authorize(final DataFetchingEnvironment environment) {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final RedoInterviewInput input = this.graphQLUtil.getInput(environment, RedoInterviewInput.class);

		if (!isSuperAdmin(authenticatedUser)
				&& !this.partnerRepAuthorizer.isPartnerForInterview(authenticatedUser, input.getInterviewId())) {
			throw new AuthorizationException("User does not have permission to redo interview");
		}

		return AuthorizationResult.builder()
				.build();
	}

	private Boolean isSuperAdmin(final AuthenticatedUser authenticatedUser) {
		return authenticatedUser.getRoles().contains(UserRole.ADMIN) ||
				authenticatedUser.getRoles().contains(UserRole.OPS);
	}
}
