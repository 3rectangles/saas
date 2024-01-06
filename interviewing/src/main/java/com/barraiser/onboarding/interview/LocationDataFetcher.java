/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.input.GetLocationsInput;
import com.barraiser.common.graphql.types.Location;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.auth.AllowAuthenticatedUserAuthorizationInputConstructor;

import com.barraiser.onboarding.dal.JobRoleRepository;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.jobrole.dal.LocationRepository;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static com.barraiser.onboarding.graphql.NamedDataFetcher.QUERY_TYPE;

@Component
@Log4j2
public class LocationDataFetcher extends AuthorizedGraphQLQuery<List<Location>> {
	private final GraphQLUtil graphQLUtil;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final JobRoleRepository jobRoleRepository;
	private final LocationRepository locationRepository;
	private final LocationMapper locationMapper;

	public LocationDataFetcher(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			ObjectFieldsFilter objectFieldsFilter,
			JobRoleRepository jobRoleRepository,
			LocationRepository locationRepository,
			LocationMapper locationMapper,
			GraphQLUtil graphQLUtil) {
		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor, objectFieldsFilter);
		this.graphQLUtil = graphQLUtil;
		this.authorizationServiceFeignClient = authorizationServiceFeignClient;
		this.jobRoleRepository = jobRoleRepository;
		this.locationRepository = locationRepository;
		this.locationMapper = locationMapper;
	}

	@Override
	protected List<Location> fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult) {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);

		final GetLocationsInput input = this.graphQLUtil.getInput(environment, GetLocationsInput.class);

		return this.locationRepository.findAllByPartnerId(input.getPartnerId())
				.stream()
				.map(this.locationMapper::toLocation)
				.collect(Collectors.toList());

	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(QUERY_TYPE, "fetchLocations"));
	}
}
