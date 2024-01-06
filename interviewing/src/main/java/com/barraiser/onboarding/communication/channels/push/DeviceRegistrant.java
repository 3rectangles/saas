/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.communication.channels.push;

import com.amazonaws.services.pinpoint.AmazonPinpoint;
import com.amazonaws.services.pinpoint.model.EndpointRequest;
import com.amazonaws.services.pinpoint.model.EndpointUser;
import com.amazonaws.services.pinpoint.model.UpdateEndpointRequest;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.UserRole;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class DeviceRegistrant {
	public static final String EXPERT_COMMUNICATION_PROJECT_ID = "d0ed69ae878a4c76a435772fa3b0e185";
	public static final String GCM = "GCM";

	private final AmazonPinpoint amazonPinpoint;

	public void registerForExpertCommunication(final String deviceToken, final AuthenticatedUser user,
			final DeviceType deviceType, final Boolean isEnabled) {
		if (DeviceType.ANDROID == deviceType) {
			this.registerAndroidDeviceWithPinPoint(EXPERT_COMMUNICATION_PROJECT_ID, deviceToken, user, isEnabled);
		} else {
			log.info("Unsupported device type {}", deviceType.toString());
		}
	}

	public void registerAndroidDeviceWithPinPoint(final String applicationId,
			final String deviceToken,
			final AuthenticatedUser user,
			final Boolean isEnabled) {

		final String endPointStatus = isEnabled ? "ACTIVE" : "INACTIVE";
		final Map attributes = Map.of("roles", user.getRoles().stream()
				.map(UserRole::getRole)
				.collect(Collectors.toList()));

		final EndpointUser endpointUser = new EndpointUser()
				.withUserId(user.getUserName())
				.withUserAttributes(attributes);

		final UpdateEndpointRequest updateEndpointRequest = new UpdateEndpointRequest()
				.withApplicationId(applicationId)
				.withEndpointRequest(new EndpointRequest()
						.withAddress(deviceToken)
						.withChannelType(GCM)
						.withEndpointStatus(endPointStatus)
						.withUser(endpointUser))
				.withEndpointId(deviceToken);

		this.amazonPinpoint.updateEndpoint(updateEndpointRequest);
	}
}
