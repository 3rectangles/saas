/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.dto;

import com.barraiser.onboarding.common.IdNameField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)

public class ExpertIssue {

	private String id;
	private String key;
	private Fields fields;

	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	@Builder(toBuilder = true)
	public static class Fields {
		@JsonProperty("issuetype")
		private IdNameField issuetype;

		@JsonProperty("status")
		private IdNameField status;

		@JsonProperty("assignee")
		private Person assignee;

		@JsonProperty("customfield_10265")
		private IdValueField reachoutChannel;

		@JsonProperty("customfield_10266")
		private IdValueField interviewerReferrer;

		@JsonProperty("customfield_10386")
		private List<IdValueField> roles;

		@JsonProperty("customfield_10261")
		private String resumeReceivedDate;

		@JsonProperty("customfield_10257")
		private IdValueField reason;

		@JsonProperty("customfield_10256")
		private List<IdValueField> lastCompanies;

		@JsonProperty("customfield_10253")
		private List<IdValueField> peerDomains;

		@JsonProperty("customfield_10249")
		private IdValueField jobRole;

		@JsonProperty("customfield_10248")
		private String resume;

		@JsonProperty("customfield_10246")
		private IdValueField targetCompany;

		@JsonProperty("customfield_10404")
		private List<IdValueField> companiesForWhichExpertCanTakeInterview;

		@JsonProperty("customfield_10245")
		private String firstName;

		@JsonProperty("customfield_10244")
		private String lastName;

		@JsonProperty("customfield_10243")
		private String phone;

		@JsonProperty("customfield_10254")
		private List<IdValueField> expertDomains;

		@JsonProperty("customfield_10242")
		private String email;

		@JsonProperty("customfield_10241")
		private IdValueField category;

		@JsonProperty("customfield_10239")
		private String almaMater;

		@JsonProperty("customfield_10238")
		private String currentCompany;

		@JsonProperty("customfield_10240")
		private IdValueField domain;

		@JsonProperty("customfield_10237")
		private int workExperience;

		@JsonProperty("customfield_10236")
		private String designation;

		@JsonProperty("customfield_10235")
		private String linkedInProfile;

		@JsonProperty("customfield_10361")
		private Double costPerHour;

		@JsonProperty("customfield_10362")
		private IdValueField currency;

		@JsonProperty("customfield_10371")
		private String IFSC;

		@JsonProperty("customfield_10372")
		private String accountNumber;

		@JsonProperty("customfield_10369")
		private IdValueField calculationLogic;

		@JsonProperty("customfield_10375")
		private IdValueField cancellationLogic;

		@JsonProperty("customfield_10367")
		private IdValueField active;

		@JsonProperty("customfield_10368")
		private Double multiplier;

		@JsonProperty("customfield_10374")
		private IdValueField consultancyReferrer;

		@JsonProperty("customfield_10391")
		private IdValueField isUnderTraining;

		@JsonProperty("customfield_10416")
		private Long gapBetweenInterviews;

		@JsonProperty("customfield_10419")
		private String whatsappNumber;

		@JsonProperty("customfield_10469")
		private IdValueField isDemoEligible;

		@JsonProperty("customfield_10467")
		private List<IdValueField> countriesForWhichExpertCanTakeInterviews;

		@JsonProperty("customfield_10466")
		private IdValueField countryThatExpertBelongsTo;

		@JsonProperty("customfield_10433")
		private IdValueField timezone;

		@JsonProperty("customfield_10475")
		private IdValueField willingToSwitchVideoOn;
	}
}
