/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.common.monitoring.Profiled;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.UserRole;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.types.UserDetails;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class GetLoggedInUserDetails implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final UserDetailsRepository userDetailsRepository;
	private final UserInformationManagementHelper userInformationManagementHelper;

	@Override
	public String name() {
		return "getLoggedInUserDetails";
	}

	@Override
	public String type() {
		return QUERY_TYPE;
	}

	@Profiled(name = "loggedInUserDetails")
	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final Optional<UserDetailsDAO> userDetailsDAO = this.userDetailsRepository
				.findById(authenticatedUser.getUserName());

		UserDetails userDetails = UserDetails.builder()
				.userName(authenticatedUser.getUserName())
				.email(authenticatedUser.getEmail())
				.phone(authenticatedUser.getPhone())
				.roles(this.userInformationManagementHelper.getRolesOfUser(authenticatedUser.getUserName()))
				.userDetailsPresent(false)
				.build();

		if (userDetailsDAO.isPresent()) {
			userDetails = userDetails.toBuilder()
					.firstName(userDetailsDAO.get().getFirstName())
					.lastName(userDetailsDAO.get().getLastName())
					.userDetailsPresent(true)
					.build();
		}

		return DataFetcherResult.newResult()
				.data(userDetails)
				.build();
	}
}
