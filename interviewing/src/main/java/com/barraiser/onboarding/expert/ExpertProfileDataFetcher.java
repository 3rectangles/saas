/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.expert;

import com.barraiser.common.graphql.input.GetExpertProfileInput;
import com.barraiser.common.graphql.types.expertProfile.ExpertProfile;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.onboarding.expert.auth.GetExpertProfileAuthorizer;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery_deprecated;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ExpertProfileDataFetcher extends AuthorizedGraphQLQuery_deprecated<ExpertProfile> {

	private final GraphQLUtil graphQLUtil;

	public ExpertProfileDataFetcher(GetExpertProfileAuthorizer expertProfileAuthorizer,
			ObjectFieldsFilter<ExpertProfile> objectFieldsFilter, GraphQLUtil graphQLUtil) {
		super(expertProfileAuthorizer, objectFieldsFilter);
		this.graphQLUtil = graphQLUtil;
	}

	@Override
	protected ExpertProfile fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult) {
		GetExpertProfileInput input = this.graphQLUtil.getInput(environment, GetExpertProfileInput.class);

		return ExpertProfile.builder()
				.expertId(input.getExpertId())
				.build();
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of("Query", "getExpertProfile"));
	}
}
