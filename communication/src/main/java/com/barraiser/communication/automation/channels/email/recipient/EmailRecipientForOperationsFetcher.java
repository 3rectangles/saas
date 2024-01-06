/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.email.recipient;

import com.barraiser.common.entity.Entity;
import com.barraiser.common.entity.EntityType;
import com.barraiser.communication.automation.channels.email.dto.EmailRecipient;
import com.barraiser.communication.automation.constants.RecipientType;
import com.barraiser.communication.automation.recipient.RecipientFetcher;
import com.barraiser.communication.common.CommunicationStaticAppConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class EmailRecipientForOperationsFetcher implements RecipientFetcher<EmailRecipient> {

	private final CommunicationStaticAppConfig communicationStaticAppConfig;

	@Override
	public RecipientType getRecipientType() {
		return RecipientType.OPERATIONS;
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.INTERVIEW;
	}

	@Override
	public EmailRecipient getRecipient(final Entity entity, String eventType) {
		final String opsEmailId = this.communicationStaticAppConfig.getInterviewLifecycleInformationEmail();
		return EmailRecipient.builder()
				.toEmails(List.of(opsEmailId))
				.build();
	}
}
