/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.expert.auth;

import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.GraphQLAbacAuthorizer;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class GetExpertProfileAuthorizer implements GraphQLAbacAuthorizer {

	private static final List<String> ALL_FIELDS = List.of(
			"expertId",
			"interviewingConfiguration");

	@Override
	public AuthorizationResult authorize(final DataFetchingEnvironment environment) {

		/**
		 * NO/OP Authorizer right now.
		 */
		return AuthorizationResult.builder().readableFields(ALL_FIELDS).build();
	}
}
