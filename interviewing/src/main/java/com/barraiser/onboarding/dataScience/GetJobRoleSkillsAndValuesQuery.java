/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dataScience;

import com.barraiser.common.graphql.input.GetJobRoleSkillsValuesInput;
import com.barraiser.common.graphql.types.GetJobRoleSkillsAndValues;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.auth.AllowAuthenticatedUserAuthorizationInputConstructor;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

import static com.barraiser.onboarding.graphql.NamedDataFetcher.QUERY_TYPE;

@Log4j2
@Component
public class GetJobRoleSkillsAndValuesQuery extends AuthorizedGraphQLQuery<GetJobRoleSkillsAndValues> {
	private final GraphQLUtil graphQLUtil;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final DataScienceFeignClient dataScienceFeignClient;

	public GetJobRoleSkillsAndValuesQuery(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			ObjectFieldsFilter objectFieldsFilter,
			DataScienceFeignClient dataScienceFeignClient,
			GraphQLUtil graphQLUtil) {
		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor, objectFieldsFilter);
		this.graphQLUtil = graphQLUtil;
		this.authorizationServiceFeignClient = authorizationServiceFeignClient;
		this.dataScienceFeignClient = dataScienceFeignClient;
	}

	@Override
	protected GetJobRoleSkillsAndValues fetch(DataFetchingEnvironment environment,
			AuthorizationResult authorizationResult) throws IOException {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);

		final GetJobRoleSkillsValuesInput input = this.graphQLUtil.getInput(environment,
				GetJobRoleSkillsValuesInput.class);

		final Long startTime = System.currentTimeMillis();
		// TODO:Remove logs
		log.info("Time before Get Skills Call: " + startTime);

		log.info(String.format(
				"Getting Interview Structure Skills and Values from DS model for text %s a",
				input.getJobRoleDescription()));

		final ResponseEntity<GetJobRoleSkillsAndValues> skillsAndValues = this.dataScienceFeignClient
				.generateSkillsAndValues(input);

		log.info("Time taken for Get Skills Call: " + (System.currentTimeMillis() - startTime));

		return skillsAndValues.getBody();
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(List.of(QUERY_TYPE, "getJobRoleSkillsValues"));
	}

}
