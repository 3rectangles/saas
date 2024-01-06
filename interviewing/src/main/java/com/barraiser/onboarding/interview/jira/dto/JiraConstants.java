/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.dto;

import java.util.List;

public final class JiraConstants {

	public static final List<String> JIRA_COMMENTS_STATUS = List.of(
			"comment_created",
			"comment_updated",
			"comment_deleted");

	private JiraConstants() {

	}
}
