/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.UserTourHistoryDAO;
import com.barraiser.onboarding.dal.UserTourHistoryRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.input.UserTourHistoryInput;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class UserTourHistoryMutation implements NamedDataFetcher<Boolean> {
	private final String AUTH_ERROR = "You do not have permissions for this page. Please login from the appropriate account";

	private final GraphQLUtil graphQLUtil;
	private final UserTourHistoryRepository userTourHistoryRepository;

	@Override
	public String name() {
		return "saveUserTourHistory";
	}

	@Override
	public String type() {
		return MUTATION_TYPE;
	}

	@Override
	public Boolean get(final DataFetchingEnvironment environment) throws Exception {

		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);

		final UserTourHistoryInput input = this.graphQLUtil.getInput(environment, UserTourHistoryInput.class);

		final UserTourHistoryDAO userTourHistoryDAO = UserTourHistoryDAO.builder().id(authenticatedUser.getUserName())
				.tourHistory(input.getHistory())
				.build();

		userTourHistoryRepository.save(userTourHistoryDAO);

		return Boolean.TRUE;
	}
}
