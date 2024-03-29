/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.whatsapp.recipient;

import com.barraiser.common.entity.Entity;
import com.barraiser.common.entity.EntityType;
import com.barraiser.common.graphql.types.Evaluation;
import com.barraiser.communication.automation.QueryDataFetcher;
import com.barraiser.communication.automation.channels.whatsapp.dto.WhatsappRecipient;
import com.barraiser.communication.automation.recipient.RecipientFetcher;
import com.barraiser.communication.automation.constants.RecipientType;
import com.barraiser.communication.automation.userSubscription.UserCommunicationSubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class WhatsappRecipientForCandidateFromEvaluationFetcher implements RecipientFetcher<WhatsappRecipient> {
	private final static String GET_EVALUATION_QUERY = "query FetchEvaluation($input: GetEvaluationInput!) {\n" +
			"    getEvaluations(input: $input) {\n" +
			"      candidate {\n" +
			"        id\n" +
			"\tphone\n" +
			"      }\n" +
			"    }\n" +
			"  }";

	private final UserCommunicationSubscriptionService userCommunicationSubscriptionService;
	private final QueryDataFetcher queryDataFetcher;
	private final ObjectMapper objectMapper;

	@Override
	public RecipientType getRecipientType() {
		return RecipientType.CANDIDATE;
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.EVALUATION;
	}

	@Override
	public WhatsappRecipient getRecipient(final Entity entity, final String eventType) {
		final Object queryData = this.queryDataFetcher.fetchQueryData(GET_EVALUATION_QUERY, entity);
		final Evaluation evaluation = this.objectMapper.convertValue(
				this.queryDataFetcher.getObjectFromPath(queryData, List.of("getEvaluations", "0")), Evaluation.class);
		final String candidateId = evaluation.getCandidate().getId();
		final String candidateWhatsappPhoneNumber = evaluation.getCandidate().getPhone();
		if (!this.userCommunicationSubscriptionService.isUserSubscribedForEvent(candidateId, eventType)) {
			return null;
		}

		return WhatsappRecipient.builder()
				.toPhoneNumber(candidateWhatsappPhoneNumber)
				.build();
	}

}
