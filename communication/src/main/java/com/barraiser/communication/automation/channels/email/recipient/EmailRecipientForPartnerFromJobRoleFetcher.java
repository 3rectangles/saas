/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.email.recipient;

import com.barraiser.common.entity.Entity;
import com.barraiser.common.entity.EntityType;
import com.barraiser.common.graphql.types.JobRole;
import com.barraiser.communication.automation.QueryDataFetcher;
import com.barraiser.communication.automation.channels.email.dto.EmailRecipient;
import com.barraiser.communication.automation.constants.RecipientType;
import com.barraiser.communication.automation.recipient.RecipientFetcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class EmailRecipientForPartnerFromJobRoleFetcher implements RecipientFetcher<EmailRecipient> {
	private final static String GET_JOB_ROLE_QUERY = "query GetJobRoles($input: GetJobRoleInput!) {\n" +
			"    getJobRoles(input: $input) {\n" +
			"        defaultPocEmail\n" +
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
		return EntityType.JOB_ROLE;
	}

	@Override
	public EmailRecipient getRecipient(Entity entity, String eventType) {
		final Object queryData = this.queryDataFetcher
				.fetchQueryData(
						GET_JOB_ROLE_QUERY,
						entity);

		final JobRole jobRole = this.objectMapper
				.convertValue(this.queryDataFetcher
						.getObjectFromPath(
								queryData,
								List.of("getJobRoles", "0")),
						JobRole.class);

		final String defaultPocEmail = jobRole.getDefaultPocEmail();

		return EmailRecipient
				.builder()
				.toEmails(List.of(defaultPocEmail))
				.build();
	}
}
