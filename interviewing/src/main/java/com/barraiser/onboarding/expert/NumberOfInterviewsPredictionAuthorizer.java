/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.expert;

import com.barraiser.common.graphql.input.PredictNumberOfInterviewsOfExpertInput;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.GraphQLAbacAuthorizer;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.user.auth.SuperAdminAuthorizer;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class NumberOfInterviewsPredictionAuthorizer implements GraphQLAbacAuthorizer {
	private final SuperAdminAuthorizer superAdminAuthorizer;
	private final GraphQLUtil graphQLUtil;

	@Override
	public AuthorizationResult authorize(final DataFetchingEnvironment environment) {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final PredictNumberOfInterviewsOfExpertInput input = this.graphQLUtil.getInput(environment,
				PredictNumberOfInterviewsOfExpertInput.class);
		if (this.superAdminAuthorizer.isSuperAdmin(authenticatedUser)
				|| input.getExpertId().equals(authenticatedUser.getUserName())) {
			return AuthorizationResult.builder()
					.readableFields(List.of("*"))
					.build();
		}
		throw new AuthorizationException("User does not have permission to view expected number of interviews");
	}
}
