/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner.partnerPricing.auth;

import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.GraphQLAbacAuthorizer;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.user.auth.SuperAdminAuthorizer;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class UpdatePartnerPricingStageDetailsAuthorizer implements GraphQLAbacAuthorizer {
	public static final String ERROR_MESSAGE_FOR_UNAUTHENTICATED_USER = "User does not have permission to update pricing stage details info";

	private final GraphQLUtil graphQLUtil;
	private final SuperAdminAuthorizer superAdminAuthorizer;

	@Override
	public AuthorizationResult authorize(final DataFetchingEnvironment environment) {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		if (!this.superAdminAuthorizer.isSuperAdmin(authenticatedUser)) {
			throw new AuthorizationException(ERROR_MESSAGE_FOR_UNAUTHENTICATED_USER);
		}
		return AuthorizationResult.builder()
				.build();
	}
}
