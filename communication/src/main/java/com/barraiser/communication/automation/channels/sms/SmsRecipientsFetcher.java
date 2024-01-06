/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.sms;

import com.barraiser.common.entity.Entity;
import com.barraiser.communication.automation.channels.sms.dto.SmsData;
import com.barraiser.communication.automation.channels.sms.dto.SmsRecipient;
import com.barraiser.communication.automation.pipeline.CommunicationProcessor;

import com.barraiser.communication.automation.recipient.RecipientFetcher;
import com.barraiser.communication.automation.constants.Channel;
import com.barraiser.communication.automation.constants.RecipientType;
import com.barraiser.communication.common.CommunicationStaticAppConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@RequiredArgsConstructor
public class SmsRecipientsFetcher implements CommunicationProcessor<SmsData> {
	private final List<RecipientFetcher<SmsRecipient>> recipientFetchers;
	private final CommunicationStaticAppConfig staticAppConfig;

	@Override
	public Channel getChannel() {
		return Channel.SMS;
	}

	@Override
	public void process(SmsData data) {
		final Entity entity = data.getInput().getEntity();
		final String eventType = data.getInput().getEventType();
		final RecipientType recipientType = data.getInput().getRecipientType();

		for (final RecipientFetcher<SmsRecipient> recipientFetcher : this.recipientFetchers) {
			if (recipientFetcher.getRecipientType().equals(recipientType) &&
					recipientFetcher.getEntityType().equals(entity.getType())) {
				SmsRecipient recipient = recipientFetcher.getRecipient(entity, eventType);
				data.setSmsRecipient(recipient);
				return;
			}
		}
	}
}
