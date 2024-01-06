/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.notifications;

import com.barraiser.communication.configurations.ConfigurationsRemover;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.input.RemoveSlackMemberInput;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SlackMutationRemover implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final ConfigurationsRemover configurationsRemover;

	@Override
	public String name() {
		return "removeSlackMember";
	}

	@Override
	public String type() {
		return MUTATION_TYPE;
	}

	@Override
	public Boolean get(final DataFetchingEnvironment environment) throws Exception {

		final RemoveSlackMemberInput input = this.graphQLUtil.getInput(environment, RemoveSlackMemberInput.class);
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);

		final Boolean isRemoved = this.configurationsRemover.removeSlackConfigurations(input.getPartnerId(),
				input.getChannel(), input.getChannelId());

		return isRemoved;
	}
}
