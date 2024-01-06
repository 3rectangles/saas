/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.expert.auth;

import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.GraphQLAbacAuthorizer;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class ExpertInterviewingConfigurationAuthorizer implements GraphQLAbacAuthorizer {

	public static final List<String> All_FIELDS = List.of(
			"timeGapBetweenInterviews");

	@Override
	public AuthorizationResult authorize(final DataFetchingEnvironment environment) {

		/**
		 * NO/OP Authorizer right now.
		 */
		return AuthorizationResult.builder().readableFields(All_FIELDS).build();
	}
}
