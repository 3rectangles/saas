/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.TargetJobAttributesDAO;
import com.barraiser.onboarding.dal.TargetJobAttributesRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GetTargetJobAttributes implements NamedDataFetcher {
	private final TargetJobAttributesRepository targetJobAttributesRepository;
	private final GraphQLUtil graphQLUtil;

	@Override
	public String name() {
		return "getTargetJobAttributes";
	}

	@Override
	public String type() {
		return QUERY_TYPE;
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);

		final TargetJobAttributesDAO targetJobAttributes = this.targetJobAttributesRepository
				.findByUserId(authenticatedUser.getUserName())
				.orElseThrow();
		return DataFetcherResult.newResult()
				.data(targetJobAttributes)
				.build();
	}
}
