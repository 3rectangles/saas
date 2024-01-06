/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.amazonaws.services.route53domains.model.CountryCode;
import com.barraiser.common.graphql.types.Country;
import com.barraiser.common.graphql.types.Location;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.auth.AllowAuthenticatedUserAuthorizationInputConstructor;
import com.barraiser.onboarding.dal.JobRoleRepository;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.evaluation.search.auth.AuthorizationInputConstructor;
import com.barraiser.onboarding.interview.jobrole.JobRoleCategory;
import com.barraiser.onboarding.interview.jobrole.dal.LocationRepository;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.barraiser.onboarding.graphql.NamedDataFetcher.QUERY_TYPE;

@Component
public class CountryDataFetcher extends AuthorizedGraphQLQuery<List<Country>> {
	private final GraphQLUtil graphQLUtil;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final JobRoleRepository jobRoleRepository;

	public CountryDataFetcher(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			ObjectFieldsFilter objectFieldsFilter,
			JobRoleRepository jobRoleRepository,
			GraphQLUtil graphQLUtil) {
		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor, objectFieldsFilter);
		this.graphQLUtil = graphQLUtil;
		this.authorizationServiceFeignClient = authorizationServiceFeignClient;
		this.jobRoleRepository = jobRoleRepository;
	}

	@Override
	protected List<Country> fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult) {
		List<String> countries = this.jobRoleRepository.findDistinctCountryCodes();
		return countries.stream().map(this::toCountry).collect(Collectors.toList());
	}

	private Country toCountry(String country) {
		return Country.builder()
				.countryCode(country)
				.name(country)
				.build();
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(QUERY_TYPE, "getCountries"));
	}
}
