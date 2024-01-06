/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user.graphql;

import com.barraiser.common.graphql.types.Interviewer;
import com.barraiser.common.graphql.types.UserDetails;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery_deprecated;
import com.barraiser.onboarding.user.UserDetailsUtilService;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InterviewerUserDetailsDataFetcher extends AuthorizedGraphQLQuery_deprecated<UserDetails> {
	private final UserDetailsUtilService userDetailsUtilService;

	public InterviewerUserDetailsDataFetcher(InterviewerUserDetailsAuthorizer abacAuthorizer,
			ObjectFieldsFilter<UserDetails> objectFieldsFilter, UserDetailsUtilService userDetailsUtilService) {
		super(abacAuthorizer, objectFieldsFilter);
		this.userDetailsUtilService = userDetailsUtilService;
	}

	@Override
	protected UserDetails fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult) {
		final Interviewer expertDetails = environment.getSource();

		return this.userDetailsUtilService.getUserDetailsWithoutRoles(expertDetails.getId());
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of("Interviewer", "userDetails"));
	}
}
