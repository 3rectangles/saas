/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner;

import com.barraiser.onboarding.auth.AuthorizationResourceDTO;
import com.barraiser.onboarding.auth.Authorizer;
import com.barraiser.onboarding.partner.auth.PartnerPortalAuthorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.PartnerRepsDAO;
import com.barraiser.onboarding.dal.PartnerRepsRepository;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.input.PartnerInput;
import com.barraiser.common.graphql.types.PartnerRepDetails;
import com.barraiser.common.graphql.types.UserDetails;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class GetPartnerReps implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final PartnerRepsRepository partnerRepsRepository;
	private final UserDetailsRepository userDetailsRepository;
	private final Authorizer authorizer;

	@Override
	public String name() {
		return "getPartnerReps";
	}

	@Override
	public String type() {
		return QUERY_TYPE;
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);

		final PartnerInput input = this.graphQLUtil.getArgument(environment, "input", PartnerInput.class);

		final AuthorizationResourceDTO authorizationResource = AuthorizationResourceDTO.builder()
				.type(PartnerPortalAuthorizer.RESOURCE_TYPE)
				.resource(input.getPartnerId())
				.build();
		this.authorizer.can(authenticatedUser, PartnerPortalAuthorizer.ACTION_READ_AND_WRITE, authorizationResource);

		return DataFetcherResult.newResult().data(this.getPartnerRepsForPartner(input.getPartnerId())).build();
	}

	private List<PartnerRepDetails> getPartnerRepsForPartner(final String partnerId) {
		final List<PartnerRepsDAO> partnerRepsDAOs = this.partnerRepsRepository.findAllByPartnerId(partnerId);
		return partnerRepsDAOs.stream().map(
				p -> {
					final UserDetailsDAO user = this.userDetailsRepository.findById(p.getPartnerRepId()).orElse(null);
					final List<String> locations = p.getLocations() != null
							? Arrays.stream(p.getLocations().split(",")).collect(Collectors.toList())
							: List.of();

					final List<String> teams = p.getTeams() != null
							? Arrays.stream(p.getTeams().split(",")).collect(Collectors.toList())
							: List.of();

					return user == null ? null
							: PartnerRepDetails.builder()
									.partnerId(partnerId)
									.accessGrantedOn(
											p.getCreatedOn() == null ? null : p.getCreatedOn().getEpochSecond())
									.userDetails(UserDetails.builder()
											.id(user.getId())
											.email(user.getEmail())
											.phone(user.getPhone())
											.firstName(user.getFirstName())
											.lastName(user.getLastName())
											.build())
									.locations(locations)
									.teams(teams)
									.roles(p.getPartnerRoles())
									.build();
				})
				.filter(Objects::nonNull)
				.collect(Collectors.toList());
	}
}
