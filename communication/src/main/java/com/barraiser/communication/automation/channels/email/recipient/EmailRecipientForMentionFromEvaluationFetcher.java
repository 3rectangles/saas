/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.email.recipient;

import com.barraiser.common.entity.Entity;
import com.barraiser.common.entity.EntityType;
import com.barraiser.communication.automation.channels.email.dto.EmailRecipient;
import com.barraiser.communication.automation.constants.RecipientType;
import com.barraiser.communication.automation.recipient.RecipientFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class EmailRecipientForMentionFromEvaluationFetcher implements RecipientFetcher<EmailRecipient> {
	// For cases where Evaluation is used as entity and recipients are mentioned in
	// event

	@Override
	public RecipientType getRecipientType() {
		return RecipientType.MENTION;
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.EVALUATION;
	}

	@Override
	public EmailRecipient getRecipient(Entity entity, String eventType) {
		return EmailRecipient.builder()
				.build();
	}
}
