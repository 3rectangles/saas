/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.common.graphql.types.UserDetails;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.UserRole;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.auth.AllowAllAuthorizationInputConstructor;
import com.barraiser.onboarding.dal.PartnerCompanyRepository;
import com.barraiser.onboarding.dal.PartnershipModelRepository;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.barraiser.onboarding.graphql.NamedDataFetcher.QUERY_TYPE;

@Log4j2
@Component
public class GetLoggedInUserForPartner extends AuthorizedGraphQLQuery<UserDetails> {
	private final GraphQLUtil graphQLUtil;
	private final UserDetailsRepository userDetailsRepository;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final UserInformationManagementHelper userInformationManagementHelper;

	public GetLoggedInUserForPartner(AuthorizationServiceFeignClient authorizationServiceFeignClient,
			AllowAllAuthorizationInputConstructor allowAllAuthorizationInputConstructor,
			ObjectFieldsFilter<UserDetails> objectFieldsFilter,
			GraphQLUtil graphQLUtil,
			UserDetailsRepository userDetailsRepository,
			PartnerCompanyRepository partnerCompanyRepository,
			UserInformationManagementHelper userInformationManagementHelper) {

		super(authorizationServiceFeignClient, allowAllAuthorizationInputConstructor, objectFieldsFilter);
		this.graphQLUtil = graphQLUtil;
		this.userDetailsRepository = userDetailsRepository;
		this.authorizationServiceFeignClient = authorizationServiceFeignClient;
		this.partnerCompanyRepository = partnerCompanyRepository;
		this.userInformationManagementHelper = userInformationManagementHelper;
	}

	@Override
	protected UserDetails fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult)
			throws IOException {
		final String partnerId = environment.getArgument("partnerId");
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final Optional<UserDetailsDAO> userDetailsDAO = this.userDetailsRepository
				.findById(authenticatedUser.getUserName());

		UserDetails userDetails = UserDetails.builder()
				.userName(authenticatedUser.getUserName())
				.email(authenticatedUser.getEmail())
				.phone(authenticatedUser.getPhone())
				.roles(this.getRoles(partnerId, authenticatedUser))
				.userDetailsPresent(false)
				.partnershipModelId(this.partnerCompanyRepository.findById(partnerId).get().getPartnershipModelId())
				.build();

		if (userDetailsDAO.isPresent()) {
			userDetails = userDetails.toBuilder()
					.firstName(userDetailsDAO.get().getFirstName())
					.lastName(userDetailsDAO.get().getLastName())
					.userDetailsPresent(true)
					.build();
		}

		return userDetails;
	}

	private List<String> getRoles(final String partnerId, final AuthenticatedUser authenticatedUser) {
		final List<String> roles = new ArrayList<>();

		// Adding all roles that exist in the authenticated user for backward
		// compatability
		roles.addAll(this.userInformationManagementHelper.getRolesOfUser(authenticatedUser.getUserName()));

		// Adding partner level roles
		if (partnerId != null) {
			roles.addAll(this.authorizationServiceFeignClient
					.getActiveUserRoles(partnerId, authenticatedUser.getUserName())
					.stream().map(r -> r.getName()).collect(Collectors.toList()));
		}

		return roles;
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(QUERY_TYPE, "getLoggedInUserDetailsForPartner"));
	}
}
