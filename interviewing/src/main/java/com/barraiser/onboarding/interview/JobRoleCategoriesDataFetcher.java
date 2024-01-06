/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.auth.AllowAuthenticatedUserAuthorizationInputConstructor;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.jobrole.JobRoleCategory;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.barraiser.onboarding.graphql.NamedDataFetcher.QUERY_TYPE;

@Component
@Log4j2
public class JobRoleCategoriesDataFetcher
		extends AuthorizedGraphQLQuery<List<com.barraiser.common.graphql.types.JobRoleCategory>> {

	private final GraphQLUtil graphQLUtil;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;

	public JobRoleCategoriesDataFetcher(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			ObjectFieldsFilter objectFieldsFilter,
			GraphQLUtil graphQLUtil) {
		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor, objectFieldsFilter);
		this.graphQLUtil = graphQLUtil;
		this.authorizationServiceFeignClient = authorizationServiceFeignClient;
	}

	@Override
	protected List<com.barraiser.common.graphql.types.JobRoleCategory> fetch(DataFetchingEnvironment environment,
			AuthorizationResult authorizationResult) {
		return Arrays.stream(JobRoleCategory.values()).map(this::toJobRole).collect(Collectors.toList());
	}

	private com.barraiser.common.graphql.types.JobRoleCategory toJobRole(JobRoleCategory jobRoleCategory) {
		return com.barraiser.common.graphql.types.JobRoleCategory.builder()
				.id(jobRoleCategory.getValue())
				.name(jobRoleCategory.getValue())
				.build();
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(QUERY_TYPE, "getJobRoleCategories"));
	}
}
