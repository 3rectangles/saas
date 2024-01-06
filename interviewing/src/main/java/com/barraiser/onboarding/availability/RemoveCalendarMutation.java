/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability;

import com.barraiser.common.graphql.input.RemoveCalendarInput;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.enums.OAuthProvider;
import com.barraiser.onboarding.availability.DTO.RemoveCalendarDTO;
import com.barraiser.onboarding.graphql.GraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class RemoveCalendarMutation implements GraphQLMutation<Boolean> {
	private final GraphQLUtil graphQLUtil;
	private final AvailabilityServiceClient availabilityServiceClient;

	@Override
	public String name() {
		return "removeCalendar";
	}

	@Override
	public Boolean get(DataFetchingEnvironment environment) throws Exception {
		final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(environment);
		final RemoveCalendarInput input = this.graphQLUtil.getInput(environment, RemoveCalendarInput.class);
		this.availabilityServiceClient.removeCalendar(user.getUserName(), RemoveCalendarDTO.builder()
				.email(input.getEmail())
				.oAuthProvider(OAuthProvider.fromString(input.getOAuthProvider()))
				.context(input.getContext())
				.build());
		return true;
	}
}
