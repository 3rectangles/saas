/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.sms.recipient;

import com.barraiser.common.entity.Entity;
import com.barraiser.common.entity.EntityType;
import com.barraiser.communication.automation.EvaluationEntityDataFetcher;
import com.barraiser.communication.automation.channels.sms.dto.SmsRecipient;
import com.barraiser.communication.automation.constants.RecipientType;
import com.barraiser.communication.automation.recipient.RecipientFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class SmsRecipientForCandidateFromInterviewFetcher implements RecipientFetcher<SmsRecipient> {
	private final SmsRecipientForCandidateFromEvaluationFetcher smsRecipientForCandidateFromEvaluationFetcher;
	private final EvaluationEntityDataFetcher evaluationEntityDataFetcher;

	@Override
	public RecipientType getRecipientType() {
		return RecipientType.CANDIDATE;
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.INTERVIEW;
	}

	@Override
	public SmsRecipient getRecipient(final Entity entity, String eventType) {
		return this.smsRecipientForCandidateFromEvaluationFetcher
				.getRecipient(this.evaluationEntityDataFetcher.getEvaluationEntity(entity), eventType);
	}
}
