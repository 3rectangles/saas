/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement;

import com.barraiser.common.graphql.input.LocationInput;
import com.barraiser.common.graphql.types.Location;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.commons.dto.ats.enums.ATSProvider;
import com.barraiser.onboarding.auth.AllowAuthenticatedUserAuthorizationInputConstructor;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.LocationMapper;
import com.barraiser.onboarding.interview.jobrole.dal.LocationDAO;
import com.barraiser.onboarding.interview.jobrole.dal.LocationRepository;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Log4j2
@Component
public class AddLocationMutation extends AuthorizedGraphQLMutation<Location> {

	private final GraphQLUtil graphQLUtil;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final LocationRepository locationRepository;
	private final LocationMapper locationMapper;

	public AddLocationMutation(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAuthenticatedUserAuthorizationInputConstructor allowAuthenticatedUserAuthorizationInputConstructor,
			LocationRepository locationRepository,
			LocationMapper locationMapper,
			GraphQLUtil graphQLUtil) {
		super(authorizationServiceFeignClient, allowAuthenticatedUserAuthorizationInputConstructor);
		this.graphQLUtil = graphQLUtil;
		this.authorizationServiceFeignClient = authorizationServiceFeignClient;
		this.locationRepository = locationRepository;
		this.locationMapper = locationMapper;
	}

	@Override
	protected Location fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult)
			throws Exception {
		/* TODO: Add Authorization */
		final LocationInput input = this.graphQLUtil.getInput(environment, LocationInput.class);

		Optional<LocationDAO> existingLocation = this.locationRepository.findByPartnerIdAndName(input.getPartnerId(),
				input.getName());

		if (existingLocation.isEmpty()) {
			final LocationDAO location = this.locationRepository.save(
					LocationDAO.builder()
							.id(UUID.randomUUID().toString())
							.name(input.getName())
							.description(input.getDescription())
							.partnerId(input.getPartnerId())
							.build());

			return this.locationMapper.toLocation(location);
		}

		return Location.builder().build();
	}

	@Override
	public String name() {
		return "addLocation";
	}
}
