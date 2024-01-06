/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.search.auth;

import com.barraiser.common.graphql.types.Partner;
import com.barraiser.commons.auth.*;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@AllArgsConstructor
@Component
public class SearchEvaluationAuthorizationInputConstructor implements AuthorizationInputConstructor {

	private final GraphQLUtil graphQLUtil;

	@Override
	public AuthorizationInput construct(final DataFetchingEnvironment dataFetchingEnvironment) {

		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(dataFetchingEnvironment);

		Map<Dimension, String> authorizationDimensions = this.getAuthorizationDimensions();

		return AuthorizationInput.builder()
				.authenticatedUser(authenticatedUser)
				.authorizationDimensions(authorizationDimensions)
				.action(Action.LIST)
				.resource(Resource.EVALUATION)
				.environment(this.getEnvironment(dataFetchingEnvironment))
				.build();
	}

	private Map<Dimension, String> getAuthorizationDimensions() {
		return Map.of(Dimension.JOB_ROLE_ID_VERSION, "");
	}

	private Map<String, Object> getEnvironment(final DataFetchingEnvironment dataFetchingEnvironment) {
		final Partner partner = dataFetchingEnvironment.getSource();
		return Map.of("partnerId", partner.getId());
	}
}
