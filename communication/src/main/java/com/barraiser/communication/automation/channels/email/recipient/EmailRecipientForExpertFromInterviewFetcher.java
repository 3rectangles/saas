/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.channels.email.recipient;

import com.barraiser.common.entity.Entity;
import com.barraiser.common.entity.EntityType;
import com.barraiser.common.graphql.types.Interview;
import com.barraiser.communication.automation.QueryDataFetcher;
import com.barraiser.communication.automation.channels.email.dto.EmailRecipient;
import com.barraiser.communication.automation.constants.RecipientType;
import com.barraiser.communication.automation.recipient.RecipientFetcher;
import com.barraiser.communication.automation.userSubscription.UserCommunicationSubscriptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class EmailRecipientForExpertFromInterviewFetcher implements RecipientFetcher<EmailRecipient> {
	private final static String GET_INTERVIEW_QUERY = "query getInterviews($input: GetInterviewsInput!) {\n" +
			"    getInterviews(input: $input) {\n" +
			"        interviewer {\n" +
			"            id\n" +
			"            userDetails {" +
			"               email" +
			"            }\n" +
			"        }\n" +
			"    }\n" +
			"}";

	private final UserCommunicationSubscriptionService userCommunicationSubscriptionService;
	private final QueryDataFetcher queryDataFetcher;
	private final ObjectMapper objectMapper;

	@Override
	public RecipientType getRecipientType() {
		return RecipientType.EXPERT;
	}

	@Override
	public EntityType getEntityType() {
		return EntityType.INTERVIEW;
	}

	@Override
	public EmailRecipient getRecipient(Entity entity, String eventType) {
		final Object queryData = this.queryDataFetcher.fetchQueryData(GET_INTERVIEW_QUERY, entity);
		final Interview interview = this.objectMapper.convertValue(
				this.queryDataFetcher.getObjectFromPath(queryData, List.of("getInterviews", "0")), Interview.class);
		if (!this.userCommunicationSubscriptionService.isUserSubscribedForEvent(interview.getInterviewer().getId(),
				eventType)) {
			return null;
		}

		return EmailRecipient.builder()
				.toEmails(List.of(interview.getInterviewer().getUserDetails().getEmail()))
				.build();
	}
}
