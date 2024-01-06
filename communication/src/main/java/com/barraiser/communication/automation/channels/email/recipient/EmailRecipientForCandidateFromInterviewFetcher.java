/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.email.recipient;

import com.barraiser.common.entity.Entity;
import com.barraiser.common.entity.EntityType;
import com.barraiser.communication.automation.EvaluationEntityDataFetcher;
import com.barraiser.communication.automation.channels.email.dto.EmailRecipient;
import com.barraiser.communication.automation.constants.RecipientType;
import com.barraiser.communication.automation.recipient.RecipientFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class EmailRecipientForCandidateFromInterviewFetcher implements RecipientFetcher<EmailRecipient> {
	private final EvaluationEntityDataFetcher evaluationEntityDataFetcher;
	private final EmailRecipientForCandidateFromEvaluationFetcher emailRecipientForCandidateFromEvaluationFetcher;

	@Override
	public RecipientType getRecipientType() {
		return RecipientType.CANDIDATE;
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.INTERVIEW;
	}

	@Override
	public EmailRecipient getRecipient(final Entity entity, String eventType) {
		return this.emailRecipientForCandidateFromEvaluationFetcher
				.getRecipient(this.evaluationEntityDataFetcher.getEvaluationEntity(entity), eventType);
	}
}
