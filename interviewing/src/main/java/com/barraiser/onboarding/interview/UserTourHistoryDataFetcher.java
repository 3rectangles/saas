/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.UserTourHistoryDAO;
import com.barraiser.onboarding.dal.UserTourHistoryRepository;
import com.barraiser.onboarding.graphql.Constants;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import graphql.GraphQLContext;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@AllArgsConstructor
@Component
public class UserTourHistoryDataFetcher implements NamedDataFetcher {
	private final UserTourHistoryRepository userTourHistoryRepository;
	private final GraphQLUtil graphQLUtil;

	@Override
	public String name() {
		return "getUserTourHistory";
	}

	@Override
	public String type() {
		return QUERY_TYPE;
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {

		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);

		final UserTourHistoryDAO userTourHistoryDAO = this.userTourHistoryRepository
				.findById(authenticatedUser.getUserName())
				.orElse(UserTourHistoryDAO.builder().build());

		return DataFetcherResult.newResult()
				.data(userTourHistoryDAO.getTourHistory())
				.build();
	}
}
