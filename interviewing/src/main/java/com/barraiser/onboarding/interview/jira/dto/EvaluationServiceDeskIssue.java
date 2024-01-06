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
public class EvaluationServiceDeskIssue {
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

		@JsonProperty("issuetype")
		private IdNameField issuetype;

		@JsonProperty("customfield_10408")
		private String entityId;

		@JsonProperty("status")
		private IdNameField status;

		@JsonProperty("customfield_10349")
		private IdValueField targetJobRole;

		@JsonProperty("summary")
		private String fullName;

		@JsonProperty("description")
		private String description;

		@JsonProperty("attachment")
		private List<IdContentField> resume;

		@JsonProperty("customfield_10358")
		private String resumeLink;

		@JsonProperty("assignee")
		private Person assignee;

		@JsonProperty("reporter")
		private Person reporter;

		@JsonProperty("requestType")
		private IdNameField requestType;

		@JsonProperty("customfield_10296")
		private String pocEmail;

		@JsonProperty("customfield_10345")
		private IdValueField timezone;

		@JsonProperty("priority")
		private IdNameField priority;

		@JsonProperty("customfield_10313")
		private IdValueField candidateInterviewType;

		@JsonProperty("customfield_10316")
		private String phone;

		@JsonProperty("customfield_10317")
		private String email;

		@JsonProperty("customfield_10318")
		private String almaMater;

		@JsonProperty("customfield_10319")
		private String currentCompany;

		@JsonProperty("customfield_10320")
		private List<IdValueField> lastCompanies;

		@JsonProperty("customfield_10321")
		private String workExperience;

		@JsonProperty("customfield_10322")
		private String designation;

		@JsonProperty("customfield_10323")
		private String linkedInProfile;

		@JsonProperty("customfield_10325")
		private List<IdValueField> evaluationErrorReported;

		@JsonProperty("customfield_10328")
		private List<IdValueField> roles;

		@JsonProperty("customfield_10396")
		private Object targetCompany;

		@JsonProperty("customfield_10397")
		private String domain;

		@JsonProperty("customfield_10002")
		private Object organization;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
		@JsonProperty("customfield_10329")
		private OffsetDateTime startDate;

		@JsonProperty("customfield_10376")
		private String jobRoleId;

		@JsonProperty("customfield_10392")
		private IdValueField syncEnvironment;

		@JsonProperty("customfield_10377")
		private String jobRoleName;

		@JsonProperty("customfield_10388")
		private IdValueField waitingReason;

		@JsonProperty("customfield_10390")
		private IdValueField cancellationReason;

		@JsonProperty("customfield_10437")
		private Long totalBarraiserRounds;

		@JsonProperty("customfield_10421")
		private List<String> priorityFlags;

		@JsonProperty("customfield_10418")
		private String whatsappConsent;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "uuuu-MM-dd'T'HH:mm:ss.SSSX")
		@JsonProperty("customfield_10314")
		private OffsetDateTime followUpDate;

		@JsonProperty("customfield_10354")
		private IdValueField waitingReasonCategory;

		@JsonProperty("customfield_10355")
		private String waitingReasonForEvaluation;

		@JsonProperty("customfield_10444")
		private IdValueField waitingClientReason;
	}
}
