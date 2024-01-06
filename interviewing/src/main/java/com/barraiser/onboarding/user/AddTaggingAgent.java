/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user;

import com.barraiser.commons.auth.UserRole;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.common.graphql.UserDetailsInput;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.graphql.DataFetcherType;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import graphql.schema.DataFetchingEnvironment;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AddTaggingAgent implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final UserInformationManagementHelper userManagement;
	private final UserDetailsRepository userDetailsRepository;

	@Override
	public String name() {
		return "addTaggingAgent";
	}

	@Override
	public String type() {
		return DataFetcherType.MUTATION.getValue();
	}

	@Override
	public Boolean get(final DataFetchingEnvironment environment) throws Exception {
		final UserDetailsInput input = this.graphQLUtil.getInput(environment, UserDetailsInput.class);
		final String userId = this.userManagement.findUserByEmail(input.getEmail()).get();

		this.userManagement.addUserRole(userId, UserRole.TAGGING_AGENT);

		final UserDetailsDAO userDetails = this.userDetailsRepository.findById(userId).get();
		this.userDetailsRepository.save(userDetails
				.toBuilder()
				.role(UserRole.TAGGING_AGENT.getRole())
				.build());

		return true;
	}
}
