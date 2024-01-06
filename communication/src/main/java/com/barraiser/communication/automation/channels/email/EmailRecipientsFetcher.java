/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.email;

import com.barraiser.common.entity.Entity;
import com.barraiser.common.graphql.types.UserDetails;
import com.barraiser.communication.automation.pipeline.CommunicationProcessor;
import com.barraiser.communication.automation.channels.email.dto.EmailData;
import com.barraiser.communication.automation.channels.email.dto.EmailRecipient;
import com.barraiser.communication.automation.recipient.RecipientFetcher;
import com.barraiser.communication.automation.constants.Channel;
import com.barraiser.communication.automation.constants.RecipientType;
import com.barraiser.communication.common.CommunicationStaticAppConfig;
import com.barraiser.communication.util.RecipientFetchingHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Log4j2
@RequiredArgsConstructor
public class EmailRecipientsFetcher implements CommunicationProcessor<EmailData> {
	private final List<RecipientFetcher<EmailRecipient>> recipientFetchers;
	private final CommunicationStaticAppConfig staticAppConfig;
	private final RecipientFetchingHelper recipientFetchingHelper;

	@Override
	public Channel getChannel() {
		return Channel.EMAIL;
	}

	@Override
	public void process(EmailData data) {
		final Entity entity = data.getInput().getEntity();
		final String eventType = data.getInput().getEventType();
		final RecipientType recipientType = data.getInput().getRecipientType();

		final List<String> recipientUserIds = this.getToEmailsList(data);

		if (!recipientUserIds.isEmpty()) {
			data.setRecipient(EmailRecipient.builder()
					.toEmails(recipientUserIds)
					.build());
		}

		for (final RecipientFetcher<EmailRecipient> recipientFetcher : this.recipientFetchers) {
			if (recipientFetcher.getRecipientType().equals(recipientType) &&
					recipientFetcher.getEntityType().equals(entity.getType())) {

				// Fetching recipients based on entity and eventType
				EmailRecipient recipient = recipientFetcher.getRecipient(entity, eventType);

				// Adding recipients from communication details Event
				List<String> toEmails = this.getToEmailsList(data);
				if (recipient.getToEmails() != null) {
					toEmails.addAll(recipient.getToEmails());
				}

				List<String> toDistinctEmails = toEmails.stream()
						.distinct()
						.collect(Collectors.toList());

				// Adding cc recipients frm communication details Event
				List<String> ccEmails = this.getCCEmailsList(data);
				if (recipient.getCcEmails() != null) {
					ccEmails.addAll(recipient.getCcEmails());
				}
				List<String> ccDistinctEmails = ccEmails.stream()
						.distinct()
						.collect(Collectors.toList());

				recipient = recipient.toBuilder()
						.toEmails(toDistinctEmails)
						.ccEmails(ccDistinctEmails)
						.build();

				data.setRecipient(recipient);
			}
		}
	}

	private List<String> getToEmailsList(EmailData data) {
		List<String> toEmails = new ArrayList<>();
		// Adding emails of users sent in email communication event
		List<String> userIdsForCommunication = RecipientFetchingHelper
				.getUserIdsForEmailCommunication(data.getInput().getEventPayload());
		if (userIdsForCommunication != null) {
			for (String recipientUserId : userIdsForCommunication) {
				toEmails.add(this.getRecipientEmail(recipientUserId));
			}
		}

		// Adding emails sent in communication event to recipients
		List<String> emailIdsForCommunication = RecipientFetchingHelper
				.getEmailIdsForCommunication(data.getInput().getEventPayload());
		if (emailIdsForCommunication != null)
			toEmails.addAll(emailIdsForCommunication);

		return toEmails;

	}

	private List<String> getCCEmailsList(EmailData data) {

		List<String> ccEmails = new ArrayList<>();
		ccEmails.add(this.staticAppConfig.getEmailFromAddress());
		final List<String> ccUsers = RecipientFetchingHelper
				.getCCUserIdsForEmailCommunication(data.getInput().getEventPayload());
		if (ccUsers != null) {
			for (String user : ccUsers) {
				ccEmails.add(this.getRecipientEmail(user));
			}
		}
		final List<String> ccEmailsFromData = RecipientFetchingHelper
				.getCCEmailIdsForEmailCommunication(data.getInput().getEventPayload());
		if (ccEmailsFromData != null)
			ccEmails.addAll(ccEmailsFromData);

		return ccEmails;

	}

	private String getRecipientEmail(final String userId) {
		final UserDetails userDetails = this.recipientFetchingHelper.getRecipientUser(userId);
		return userDetails.getEmail();
	}

}
