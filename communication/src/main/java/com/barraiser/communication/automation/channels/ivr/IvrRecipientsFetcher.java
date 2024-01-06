/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.ivr;

import com.barraiser.common.entity.Entity;
import com.barraiser.communication.automation.channels.ivr.dto.IvrData;
import com.barraiser.communication.automation.channels.ivr.dto.IvrRecipient;
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
public class IvrRecipientsFetcher implements CommunicationProcessor<IvrData> {
	private final List<RecipientFetcher<IvrRecipient>> recipientFetchers;

	@Override
	public Channel getChannel() {
		return Channel.IVR;
	}

	@Override
	public void process(IvrData data) {
		final Entity entity = data.getInput().getEntity();
		final String eventType = data.getInput().getEventType();
		final RecipientType recipientType = data.getInput().getRecipientType();

		for (final RecipientFetcher<IvrRecipient> recipientFetcher : this.recipientFetchers) {
			if (recipientFetcher.getRecipientType().equals(recipientType) &&
					recipientFetcher.getEntityType().equals(entity.getType())) {
				IvrRecipient recipient = recipientFetcher.getRecipient(entity, eventType);
				data.setIvrRecipient(recipient);
				return;
			}
		}
	}
}
