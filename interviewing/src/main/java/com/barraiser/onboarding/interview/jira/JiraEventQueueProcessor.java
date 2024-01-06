/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira;

import com.amazonaws.services.sqs.model.Message;
import com.barraiser.common.monitoring.Profiled;
import com.barraiser.commons.eventing.sqs.SQSMessageHandler;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.interview.jira.dto.GenericIssue;
import com.barraiser.onboarding.interview.jira.dto.JiraEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Pulls jira events from SQS and processes it.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class JiraEventQueueProcessor implements SQSMessageHandler {
	private final List<JiraEventHandler> jiraEventHandlers;
	private final ObjectMapper objectMapper;
	private final StaticAppConfigValues staticAppConfigValues;

	@Profiled(name = "jira-event-processing")
	@Override
	public void handle(final Message message) throws Exception {
		final JiraEvent jiraEvent = this.objectMapper.readValue(message.getBody(), JiraEvent.class);
		log.info("Jira issue: {}, project: {}", jiraEvent.getIssue(), jiraEvent.getProject());

		if (Strings.isBlank(jiraEvent.getIssue())) {
			log.warn("empty Jira issue received");
			return;
		}

		final GenericIssue issue = jiraEvent.getBody().getIssue();
		if (issue != null
				&& issue.getFields() != null
				&& issue.getFields().getIssuetype() != null) {
			this.jiraEventHandlers.stream()
					.filter(x -> x.projectId().equals(issue.getFields().getIssuetype().getId()))
					.findFirst()
					.orElseThrow(
							() -> new IllegalArgumentException(
									"No handler found for the given event type"))
					.handleEvent(jiraEvent);
		} else {
			log.warn("Problematic Jira {}", message.getBody());
		}
	}

	@Override
	public String queueUrl() {
		return this.staticAppConfigValues.getJiraEventSQSUrl();
	}
}
