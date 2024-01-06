/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JiraEvent {
	private String issue;
	private String project;

	@JsonProperty("body")
	private Body body;

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder(toBuilder = true)
	public static class Body {
		@JsonProperty("webhookEvent")
		private String webhookEvent;

		@JsonProperty("comment")
		private JiraCommentDTO comment;

		@JsonProperty("timestamp")
		private Long timestamp;

		@JsonProperty("issue")
		private GenericIssue issue;
	}
}
