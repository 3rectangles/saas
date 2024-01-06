/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.graphql.Constants;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@AllArgsConstructor
public class VerifyAuthenticationStatus implements NamedDataFetcher<Boolean> {
	@Override
	public Boolean get(final DataFetchingEnvironment environment) throws Exception {
		final GraphQLContext context = environment.getContext();
		final AuthenticatedUser user = context.get(Constants.CONTEXT_KEY_USER);
		return user != null;
	}

	@Override
	public String name() {
		return "isAuthenticated";
	}

	@Override
	public String type() {
		return QUERY_TYPE;
	}
}
