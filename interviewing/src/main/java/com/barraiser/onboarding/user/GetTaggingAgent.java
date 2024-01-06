/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.commons.auth.UserRole;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

import com.barraiser.commons.auth.AuthenticatedUser;

@Log4j2
@Component
@AllArgsConstructor
public class GetTaggingAgent implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final UserDetailsRepository userDetailsRepository;

	@Override
	public String name() {
		return "getTaggingAgent";
	}

	@Override
	public String type() {
		return QUERY_TYPE;
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		if (authenticatedUser == null) {
			return DataFetcherResult.newResult().data(false).build();
		}
		final List<UserDetailsDAO> userDetailsDAOs = this.userDetailsRepository
				.findByRoleContaining(
						UserRole.TAGGING_AGENT.getRole());

		return DataFetcherResult.newResult().data(userDetailsDAOs).build();
	}
}
