/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.whatsapp;

import com.barraiser.common.entity.Entity;
import com.barraiser.common.entity.EntityType;
import com.barraiser.common.graphql.types.UserDetails;
import com.barraiser.communication.automation.QueryDataFetcher;
import com.barraiser.communication.automation.channels.whatsapp.dto.WhatsappData;
import com.barraiser.communication.automation.channels.whatsapp.dto.WhatsappRecipient;
import com.barraiser.communication.automation.pipeline.CommunicationProcessor;
import com.barraiser.communication.automation.recipient.RecipientFetcher;
import com.barraiser.communication.automation.constants.Channel;
import com.barraiser.communication.automation.constants.RecipientType;
import com.barraiser.communication.util.RecipientFetchingHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;

@Component
@Log4j2
@RequiredArgsConstructor
public class WhatsappRecipientsFetcher implements CommunicationProcessor<WhatsappData> {
	private final static String GET_USER_DETAILS_QUERY = "query getUserDetails($input: GetUserDetailsInput!) {\n" +
			"    getUserDetails(input: $input) {\n" +
			"        phone\n" +
			"        whatsappNumber\n" +
			"    }\n" +
			"}";
	private final List<RecipientFetcher<WhatsappRecipient>> recipientFetchers;
	private final QueryDataFetcher queryDataFetcher;
	private final ObjectMapper objectMapper;
	private final RecipientFetchingHelper recipientFetchingHelper;

	@Override
	public Channel getChannel() {
		return Channel.WHATSAPP;
	}

	@Override
	public void process(WhatsappData data) {
		final Entity entity = data.getInput().getEntity();
		final String eventType = data.getInput().getEventType();
		final RecipientType recipientType = data.getInput().getRecipientType();
		final String userId = RecipientFetchingHelper.getUserIdsForCommunication(data.getInput().getEventPayload());
		if (userId != null) {
			data.setWhatsappRecipient(WhatsappRecipient.builder()
					.toPhoneNumber(this.getRecipientWhatsappNumber(userId))
					.build());
			return;
		}

		for (final RecipientFetcher<WhatsappRecipient> recipientFetcher : this.recipientFetchers) {
			if (recipientFetcher.getRecipientType().equals(recipientType) &&
					recipientFetcher.getEntityType().equals(entity.getType())) {
				WhatsappRecipient recipient = recipientFetcher.getRecipient(entity, eventType);
				data.setWhatsappRecipient(recipient);
				return;
			}
		}
	}

	private String getRecipientWhatsappNumber(final String userId) {
		final UserDetails userDetails = this.recipientFetchingHelper.getRecipientUser(userId);
		return userDetails.getWhatsappNumber() != null ? userDetails.getWhatsappNumber() : userDetails.getPhone();
	}
}
