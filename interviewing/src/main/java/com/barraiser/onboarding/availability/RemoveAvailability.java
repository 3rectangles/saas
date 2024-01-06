/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.availability.DTO.RemoveAvailabilitySlotDTO;
import com.barraiser.onboarding.graphql.GraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.commons.auth.UserRole;

import graphql.schema.DataFetchingEnvironment;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RemoveAvailability implements GraphQLMutation<Boolean> {
	private final AvailabilityServiceClient availabilityServiceClient;
	private final GraphQLUtil graphQLUtil;

	public String name() {
		return "removeAvailability";
	}

	@Override
	public Boolean get(final DataFetchingEnvironment environment) throws Exception {

		final RemoveAvailabilityInput input = this.graphQLUtil.getInput(environment, RemoveAvailabilityInput.class);

		final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(environment);

		final String userId;
		if (input.getUserId() != null
				&& (user.getRoles().contains(UserRole.ADMIN)
						|| user.getRoles().contains(UserRole.OPS))) {
			// this check is to ensure only admin and ops can delete availability on behalf
			// of an
			// expert.
			userId = input.getUserId();
		} else {
			userId = user.getUserName();
		}

		input.getAvailabilities()
				.forEach(i -> this.availabilityServiceClient.removeAvailability(userId,
						RemoveAvailabilitySlotDTO.builder().startDate(i.getStartDate()).build()));
		return true;
	}
}
