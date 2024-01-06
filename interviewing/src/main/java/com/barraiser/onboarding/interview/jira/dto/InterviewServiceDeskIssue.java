/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.dto;

import com.barraiser.onboarding.common.IdNameField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class InterviewServiceDeskIssue {

	private String id;
	private String key;
	private Fields fields;

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder(toBuilder = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Fields {

		@JsonProperty("project")
		private IdNameField project;

		@JsonProperty("summary")
		private String summary;

		@JsonProperty("issuetype")
		private IdNameField issuetype;

		@JsonProperty("customfield_10409")
		private String entityId;

		@JsonProperty("issuelinks")
		private List<IdLinkedIssueField> linkedEvaluations;

		@JsonProperty("assignee")
		private Person assignee;

		@JsonProperty("status")
		private IdNameField status;

		@JsonProperty("customfield_10392")
		private IdValueField syncEnvironment;

		@JsonProperty("customfield_10335")
		private IdValueField interviewRound;

		@JsonProperty("customfield_10373")
		private String submittedCodeLink;

		@JsonProperty("customfield_10331")
		private IdValueField cancellationReason;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
		@JsonProperty("customfield_10353")
		private OffsetDateTime cancellationTime;

		@JsonProperty("customfield_10338")
		private IdValueField taggingAgent;

		@JsonProperty("customfield_10339")
		private IdValueField taggingQuality;

		@JsonProperty("customfield_10363")
		private List<Object> isRescheduled;

		@JsonProperty("customfield_10443")
		private List<Object> isBadQuality;

		@JsonProperty("customfield_10364")
		private String rescheduledFrom;

		@JsonProperty("customfield_10340")
		private List<IdValueField> interviewErrorReported;

		@JsonProperty("customfield_10341")
		private String errorDescription;

		@JsonProperty("customfield_10406")
		private String interviewStructureId;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
		@JsonProperty("customfield_10414")
		private OffsetDateTime scheduledTime;

		@JsonProperty("customfield_10421")
		private List<String> priorityFlags;

		@JsonProperty("customfield_10474")
		private String rescheduleCount;
	}
}
