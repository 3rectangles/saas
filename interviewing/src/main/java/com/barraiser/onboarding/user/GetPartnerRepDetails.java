/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.common.graphql.types.UserDetails;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.UserRole;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.types.PartnerRepDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class GetPartnerRepDetails implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final PartnerRepsRepository partnerRepsRepository;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final UserDetailsRepository userDetailsRepository;

	@Override
	public String name() {
		return "getPartnerRepDetails";
	}

	@Override
	public String type() {
		return QUERY_TYPE;
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);

		if (!authenticatedUser.getRoles().contains(UserRole.PARTNER)) {
			throw new IllegalArgumentException("User is not a partner");
		}

		final Optional<PartnerRepsDAO> partnerRepsDAO = this.partnerRepsRepository
				.findTopByPartnerRepId(authenticatedUser.getUserName());

		if (partnerRepsDAO.isEmpty())
			return DataFetcherResult.newResult().data(null).build();

		final Optional<PartnerCompanyDAO> partnerCompanyDAO = this.partnerCompanyRepository
				.findById(partnerRepsDAO.get().getPartnerId());

		final Optional<UserDetailsDAO> userDetailsDAO = this.userDetailsRepository
				.findById(partnerRepsDAO.get().getPartnerRepId());

		final UserDetails userDetails = this.getUserDetails(userDetailsDAO);
		final List<String> locations = partnerRepsDAO.get().getLocations() != null
				? Arrays.stream(partnerRepsDAO.get().getLocations().split(",")).collect(Collectors.toList())
				: List.of();

		final List<String> teams = partnerRepsDAO.get().getTeams() != null
				? Arrays.stream(partnerRepsDAO.get().getTeams().split(",")).collect(Collectors.toList())
				: List.of();

		final PartnerRepDetails partnerRepDetails = PartnerRepDetails.builder()
				.partnerId(partnerRepsDAO.get().getPartnerId())
				.companyId(partnerCompanyDAO.get().getCompanyId())
				.userDetails(userDetails)
				.locations(locations)
				.teams(teams)
				.accessGrantedOn(partnerRepsDAO.get().getCreatedOn() == null ? null
						: partnerRepsDAO.get().getCreatedOn().getEpochSecond())
				.roles(partnerRepsDAO.get().getPartnerRoles())
				.build();

		return DataFetcherResult.newResult().data(partnerRepDetails).build();
	}

	private UserDetails getUserDetails(final Optional<UserDetailsDAO> userDetailsDAO) {
		final UserDetails userDetails = UserDetails.builder()
				.id(userDetailsDAO.get().getId())
				.firstName(userDetailsDAO.get().getFirstName())
				.lastName(userDetailsDAO.get().getLastName())
				.email(userDetailsDAO.get().getEmail())
				.currentCompanyName(userDetailsDAO.get().getCurrentCompanyName())
				.workExperienceInMonths(userDetailsDAO.get().getWorkExperienceInMonths())
				.category(userDetailsDAO.get().getCategory())
				.phone(userDetailsDAO.get().getPhone())
				.build();
		return userDetails;
	}
}
