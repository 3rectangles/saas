/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.email.recipient;

import com.barraiser.common.entity.Entity;
import com.barraiser.common.entity.EntityType;
import com.barraiser.common.graphql.types.Evaluation;
import com.barraiser.common.graphql.types.JobRole;
import com.barraiser.communication.automation.QueryDataFetcher;
import com.barraiser.communication.automation.channels.email.dto.EmailRecipient;
import com.barraiser.communication.automation.constants.RecipientType;
import com.barraiser.communication.automation.recipient.RecipientFetcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class EmailRecipientForPartnerFromInterviewFetcher implements RecipientFetcher<EmailRecipient> {
	private final static String GET_POC_EMAIL_QUERY = "query getInterviews($input: GetInterviewsInput!) {\n" +
			"    getInterviews(input: $input) {\n" +
			"        evaluation {\n" +
			"           pocEmail\n" +
			"        }\n" +
			"    }\n" +
			"}";

	private final QueryDataFetcher queryDataFetcher;
	private final ObjectMapper objectMapper;

	@Override
	public RecipientType getRecipientType() {
		return RecipientType.PARTNER;
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.INTERVIEW;
	}

	@Override
	public EmailRecipient getRecipient(Entity entity, String eventType) {
		final Object queryData = this.queryDataFetcher
				.fetchQueryData(
						GET_POC_EMAIL_QUERY,
						entity);

		final Evaluation evaluation = this.objectMapper
				.convertValue(this.queryDataFetcher
						.getObjectFromPath(
								queryData,
								List.of("getInterviews", "0", "evaluation")),
						Evaluation.class);

		final String pocEmails = evaluation.getPocEmail();

		return EmailRecipient
				.builder()
				.toEmails(Arrays.asList(pocEmails.split("\\s*,\\s*")))
				.build();
	}
}
