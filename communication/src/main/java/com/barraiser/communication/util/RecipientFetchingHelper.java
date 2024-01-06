/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.util;

import com.barraiser.common.entity.Entity;
import com.barraiser.common.entity.EntityType;
import com.barraiser.common.graphql.types.UserDetails;
import com.barraiser.communication.automation.QueryDataFetcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@AllArgsConstructor
@Component
public class RecipientFetchingHelper {

	private QueryDataFetcher queryDataFetcher;
	private ObjectMapper objectMapper;

	private final static String GET_USER_DETAILS_QUERY = "query getUserDetails($input: GetUserDetailsInput!) {\n" +
			"    getUserDetails(input: $input) {\n" +
			"        phone\n" +
			"        email\n" +
			"        whatsappNumber\n" +
			"    }\n" +
			"}";

	public static String getUserIdsForCommunication(final Object eventPayload) {
		final HashMap<String, Object> payload = (HashMap<String, Object>) eventPayload;
		if (payload.containsKey("communicationDetails")) {
			final HashMap<String, Object> communicationDetails = (HashMap<String, Object>) payload
					.get("communicationDetails");
			return communicationDetails.get("userId") == null ? null : (String) communicationDetails.get("userId");
		}
		return null;
	}

	public static List<String> getUserIdsForEmailCommunication(final Object eventPayload) {
		final HashMap<String, Object> payload = (HashMap<String, Object>) eventPayload;
		if (payload.containsKey("communicationDetails")) {
			final HashMap<String, Object> communicationDetails = (HashMap<String, Object>) payload
					.get("communicationDetails");

			if (communicationDetails.get("userId") == null) {
				return null;
			}
			List<String> ids = new ArrayList<>();
			ids.add((String) communicationDetails.get("userId"));
			return ids;

		} else if (payload.containsKey("emailCommunicationDetails")) {
			final HashMap<String, Object> communicationDetails = (HashMap<String, Object>) payload
					.get("emailCommunicationDetails");

			return communicationDetails.get("toUserIds") == null ? null
					: (List<String>) communicationDetails.get("toUserIds");
		}
		return null;
	}

	public static List<String> getEmailIdsForCommunication(final Object eventPayload) {
		final HashMap<String, Object> payload = (HashMap<String, Object>) eventPayload;
		if (payload.containsKey("emailCommunicationDetails")) {
			final HashMap<String, Object> communicationDetails = (HashMap<String, Object>) payload
					.get("emailCommunicationDetails");
			return communicationDetails.get("toEmailIds") == null ? null
					: (List<String>) communicationDetails.get("toEmailIds");
		}
		return null;
	}

	public static List<String> getCCUserIdsForEmailCommunication(final Object eventPayload) {
		final HashMap<String, Object> payload = (HashMap<String, Object>) eventPayload;
		if (payload.containsKey("emailCommunicationDetails")) {
			final HashMap<String, Object> communicationDetails = (HashMap<String, Object>) payload
					.get("emailCommunicationDetails");
			return communicationDetails.get("ccUserIds") == null ? null
					: (List<String>) communicationDetails.get("ccUserIds");
		}
		return null;
	}

	public static List<String> getCCEmailIdsForEmailCommunication(final Object eventPayload) {
		final HashMap<String, Object> payload = (HashMap<String, Object>) eventPayload;
		if (payload.containsKey("emailCommunicationDetails")) {
			final HashMap<String, Object> communicationDetails = (HashMap<String, Object>) payload
					.get("emailCommunicationDetails");
			return communicationDetails.get("ccEmailIds") == null ? null
					: (List<String>) communicationDetails.get("ccEmailIds");
		}
		return null;
	}

	public UserDetails getRecipientUser(final String userId) {
		final Object queryData = this.queryDataFetcher.fetchQueryData(GET_USER_DETAILS_QUERY,
				Entity.builder().id(userId).type(EntityType.USER).build());
		return this.objectMapper.convertValue(
				this.queryDataFetcher.getObjectFromPath(queryData, List.of("getUserDetails")), UserDetails.class);
	}
}
