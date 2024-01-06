/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.events.tracking.services;

import com.barraiser.common.graphql.types.UserDetails;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.dal.PartnerCompanyRepository;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.user.UserInformationManagementHelper;
import com.google.api.client.json.Json;
import com.mixpanel.mixpanelapi.ClientDelivery;
import com.mixpanel.mixpanelapi.MessageBuilder;
import com.mixpanel.mixpanelapi.MixpanelAPI;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.barraiser.onboarding.common.Constants.*;

@AllArgsConstructor
@Component
public class MixpanelService {

	@Value("${mixpanel.project-barraiser.token}")
	private String mixpanelProjectBarraiserToken;

	private UserDetailsRepository userDetailsRepository;
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final UserInformationManagementHelper userInformationManagementHelper;

	/**
	 * @param eventName
	 * @param event
	 * @param context
	 * @throws IOException
	 */
	public void trackEvent(final String userId, final String partnerId, final AuthenticatedUser authenticatedUser,
			final String eventName, final String event,
			final Map<String, Object> context) throws IOException {
		MessageBuilder messageBuilder = new MessageBuilder(this.mixpanelProjectBarraiserToken);

		final String userTrackingId = userId != null ? userId
				: "anonymous_" + UUID.randomUUID();

		JSONObject mixpanelEvent = this.constructMixpanelEvent(event, context);

		// Create an event
		JSONObject sentEvent = messageBuilder.event(userTrackingId, eventName, mixpanelEvent);

		ClientDelivery delivery = new ClientDelivery();
		delivery.addMessage(sentEvent);

		// Use an instance of MixpanelAPI to send the messages to Mixpanel's servers.
		MixpanelAPI mixpanel = new MixpanelAPI();
		mixpanel.deliver(delivery);

		// Track user-specific actions
		if (userId != null) {

			// TBD: Send email id
			JSONObject mixpanelUser = this.constructMixpanelUser(userId, partnerId, context);
			JSONObject update = messageBuilder.set(userId, mixpanelUser);
			mixpanel.sendMessage(update);
		}
	}

	private JSONObject constructMixpanelEvent(
			final String event,
			final Map<String, Object> context) {

		final String uniqueEventId = UUID.randomUUID().toString();
		JSONObject mixpanelEvent = new JSONObject(event).put("$insert_id", uniqueEventId);

		if (context.containsKey(CONTEXT_KEY_USER_AGENT)) {
			mixpanelEvent = mixpanelEvent.put("user_agent", context.get(CONTEXT_KEY_USER_AGENT));
		}
		if (context.containsKey(CONTEXT_KEY_SOURCE_IP)) {
			mixpanelEvent = mixpanelEvent.put("$ip", context.get(CONTEXT_KEY_SOURCE_IP));
		}

		return mixpanelEvent;

	}

	private JSONObject constructMixpanelUser(final String userId, final String partnerId,
			final Map<String, Object> context) {

		final Optional<UserDetailsDAO> userDetailsDAO = this.userDetailsRepository
				.findById(userId);

		UserDetails userDetails = UserDetails.builder()
				.userName(userId)
				.email(userDetailsDAO.get().getEmail())
				.phone(userDetailsDAO.get().getPhone())
				.roles(this.getRoles(partnerId, userId))
				.partnershipModelId(partnerId != null
						? this.partnerCompanyRepository.findById(partnerId).get().getPartnershipModelId()
						: null)
				.build();

		String fullName = userDetailsDAO.get().getFirstName() + " "
				+ (userDetailsDAO.get().getLastName() != null ? userDetailsDAO.get().getLastName() : "");

		JSONObject mixpanelUser = new JSONObject(userDetails).put("$distinct_id", userId)
				.put("$email", userDetails.getEmail())
				.put("$name", fullName);

		if (context.containsKey(CONTEXT_KEY_SOURCE_IP)) {
			mixpanelUser = mixpanelUser.put("$ip", context.get(CONTEXT_KEY_SOURCE_IP));
		}

		return mixpanelUser;
	}

	private List<String> getRoles(final String partnerId, final String userId) {
		final List<String> roles = new ArrayList<>();

		// Adding all roles that exist in the authenticated user for backward
		// compatability
		roles.addAll(this.userInformationManagementHelper.getRolesOfUser(userId));

		// Adding partner level roles
		if (partnerId != null) {
			roles.addAll(this.authorizationServiceFeignClient
					.getActiveUserRoles(partnerId, userId)
					.stream().map(r -> r.getName()).collect(Collectors.toList()));
		}

		return roles;
	}

}
