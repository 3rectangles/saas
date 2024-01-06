/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.AuthorizationInput;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.evaluation.search.auth.AuthorizationInputConstructor;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AllowAllAuthorizationInputConstructor implements AuthorizationInputConstructor {

	private GraphQLUtil graphQLUtil;

	@Override
	public AuthorizationInput construct(final DataFetchingEnvironment dataFetchingEnvironment) {

		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(dataFetchingEnvironment);
		return AuthorizationInput.builder()
				.shouldAllowAll(Boolean.TRUE)
				.build();
	}

}
