/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira;

import com.barraiser.onboarding.interview.jira.client.JiraClient;
import com.barraiser.onboarding.interview.jira.dto.JiraChangeLogsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@RequiredArgsConstructor
public class JiraFieldManager {
	private final JiraClient jiraClient;

	private List<JiraChangeLogsResponse.ChangeLog> getChangeLogs(final String jiraKey) {
		final List<JiraChangeLogsResponse.ChangeLog> changeLogs = new ArrayList<>();
		while (true) {
			final JiraChangeLogsResponse response = this.jiraClient.getChangeLogs(jiraKey, changeLogs.size());
			changeLogs.addAll(response.getValues());
			if (response.getIsLast()) {
				break;
			}
		}
		return changeLogs;
	}

	public List<JiraChangeLogsResponse.ChangeLog> getChangeLogsForField(final String jiraKey, final String fieldId) {
		final List<JiraChangeLogsResponse.ChangeLog> changeLogs = this.getChangeLogs(jiraKey);
		final List<JiraChangeLogsResponse.ChangeLog> filteredChangeLogs = new ArrayList<>();
		for (JiraChangeLogsResponse.ChangeLog changeLog : changeLogs) {
			final JiraChangeLogsResponse.ChangeLog filteredChangeLog = changeLog.toBuilder()
					.items(changeLog.getItems().stream()
							.filter(x -> (fieldId.equals(x.getFieldId()) || fieldId.equals(x.getToString())))
							.collect(Collectors.toList()))
					.build();
			if (filteredChangeLog.getItems().size() > 0) {
				filteredChangeLogs.add(filteredChangeLog);
			}
		}
		return filteredChangeLogs;
	}
}
