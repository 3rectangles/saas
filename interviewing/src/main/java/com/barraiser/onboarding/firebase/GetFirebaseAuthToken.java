/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.firebase;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class GetFirebaseAuthToken implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final FirebaseManager firebaseManager;

	@Override
	public String name() {
		return "getFirebaseAuthToken";
	}

	@Override
	public String type() {
		return QUERY_TYPE;
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);

		return firebaseManager.getFirebaseAuthToken(authenticatedUser);
	}
}
