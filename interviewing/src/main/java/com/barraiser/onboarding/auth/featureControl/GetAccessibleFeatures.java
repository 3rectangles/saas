/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.featureControl;

import com.barraiser.common.graphql.input.GetAccessibleFeaturesInput;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.commons.dto.GetAccessibleFeaturesRequestDTO;
import com.barraiser.onboarding.auth.AllowAuthenticatedUserAuthorizationInputConstructor;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.barraiser.onboarding.graphql.NamedDataFetcher.QUERY_TYPE;

@Component
public class GetAccessibleFeatures extends AuthorizedGraphQLQuery<List<String>> {

	private final GraphQLUtil graphQLUtil;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;

	private static final String COMPONENT_NAME_CUSTOMER_PORTAL = "CUSTOMER_PORTAL";

	public GetAccessibleFeatures(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			ObjectFieldsFilter objectFieldsFilter,
			GraphQLUtil graphQLUtil) {
		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor, objectFieldsFilter);
		this.graphQLUtil = graphQLUtil;
		this.authorizationServiceFeignClient = authorizationServiceFeignClient;
	}

	@Override
	protected List<String> fetch(final DataFetchingEnvironment environment,
			final AuthorizationResult authorizationResult) {

		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final GetAccessibleFeaturesInput getAccessibleFeaturesInput = this.graphQLUtil.getInput(environment,
				GetAccessibleFeaturesInput.class);

		final List<String> accessibleFeaturesForUser = this
				.getAccessibleFeaturesForUser(authenticatedUser, getAccessibleFeaturesInput);

		return accessibleFeaturesForUser;
	}

	private List<String> getAccessibleFeaturesForUser(AuthenticatedUser authenticatedUser,
			GetAccessibleFeaturesInput getAccessibleFeaturesInput) {
		return this.authorizationServiceFeignClient
				.getAccessibleFeaturesForUser(authenticatedUser.getUserName(), GetAccessibleFeaturesRequestDTO.builder()
						.partnerId(getAccessibleFeaturesInput.getPartnerId())
						.componentType(getAccessibleFeaturesInput.getParentComponentOfFeatures())
						.build());
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(List.of(QUERY_TYPE, "getAccessibleFeatures"));
	}

}
