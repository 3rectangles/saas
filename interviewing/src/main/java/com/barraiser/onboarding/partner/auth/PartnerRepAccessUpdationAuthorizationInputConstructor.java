/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner.auth;

import com.barraiser.common.graphql.input.PartnerInput;
import com.barraiser.commons.auth.*;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.evaluation.search.auth.AuthorizationInputConstructor;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@AllArgsConstructor
@Component
public class PartnerRepAccessUpdationAuthorizationInputConstructor implements AuthorizationInputConstructor {

	private final GraphQLUtil graphQLUtil;

	@Override
	public AuthorizationInput construct(final DataFetchingEnvironment dataFetchingEnvironment) {

		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(dataFetchingEnvironment);

		Map<Dimension, String> authorizationDimensions = this.getAuthorizationDimensions();

		/**
		 * TBD: Figure out a way to inject action and resource in abstract authorized
		 * class
		 * or we will have to put one input constructor for each action.
		 */
		return AuthorizationInput.builder()
				.authenticatedUser(authenticatedUser)
				.authorizationDimensions(authorizationDimensions)
				.action(Action.WRITE)
				.resource(Resource.PARTNER_REP)
				.environment(this.getEnvironment(dataFetchingEnvironment))
				.build();
	}

	private Map<Dimension, String> getAuthorizationDimensions() {
		return Map.of();
	}

	private Map<String, Object> getEnvironment(final DataFetchingEnvironment dataFetchingEnvironment) {
		final PartnerInput input = this.graphQLUtil.getArgument(dataFetchingEnvironment, "input", PartnerInput.class);
		return Map.of("partnerId", input.getPartnerId());
	}
}
