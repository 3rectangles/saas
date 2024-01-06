/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.notifications;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.communication.events.StoreEventData;
import com.barraiser.common.graphql.input.StoreEventInput;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SlackEventMutation implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final StoreEventData storeEventData;

	@Override
	public String name() {
		return "storeEvent";
	}

	@Override
	public String type() {
		return MUTATION_TYPE;
	}

	@Override
	public Boolean get(final DataFetchingEnvironment environment) throws Exception {
		final StoreEventInput input = this.graphQLUtil.getInput(environment, StoreEventInput.class);

		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);

		final Boolean saveEventData = this.storeEventData.storeEvents(input.getPartnerId(), input.getChannel(),
				input.getEvent());

		return saveEventData;

	}

}
