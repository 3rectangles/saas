/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.communication.channels.push.graphql;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.communication.channels.push.DeviceRegistrant;
import com.barraiser.onboarding.graphql.GraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class PushNotificationDeviceRegistrationMutation implements GraphQLMutation {
	private final DeviceRegistrant deviceRegistration;
	private final GraphQLUtil graphQLUtil;

	@Override
	public String name() {
		return "registerDeviceForPushNotification";
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(environment);

		final DeviceRegistrationInput input = this.graphQLUtil.getInput(environment, DeviceRegistrationInput.class);

		if (input.getScope().equals("EXPERT")) {
			this.deviceRegistration.registerForExpertCommunication(
					input.getDeviceToken(),
					user,
					input.getDeviceType(),
					input.getEnabled());
		} else {
			log.error("Unknown scope detected {}", input.getScope());
		}

		return DataFetcherResult.newResult()
				.data(Boolean.TRUE)
				.build();
	}
}
