/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.smartRecruiters.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateDTO {
	@JsonProperty("id")
	private String id;

	@JsonProperty("internal")
	private Boolean internal;

	@JsonProperty("firstName")
	private String firstName;

	@JsonProperty("lastName")
	private String lastName;

	@JsonProperty("email")
	private String email;

	@JsonProperty("phoneNumber")
	private String phoneNumber;

	@JsonProperty("location")
	private Object location;

	@JsonProperty("web")
	private Object web;

	@JsonProperty("createdOn")
	private String createdOn;

	@JsonProperty("updatedOn")
	private String updatedOn;

	@JsonProperty("experience")
	private List<Object> experience;

	@JsonProperty("actions")
	private Object actions;

	@JsonProperty("primaryAssignment")
	private PrimaryAssignment primaryAssignment;

	@JsonProperty("secondaryAssignments")
	private List<Object> secondaryAssignments;

	@SuperBuilder(toBuilder = true)
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class PrimaryAssignment {
		@JsonProperty("status")
		private String status;

		@JsonProperty("subStatus")
		private String subStatus;

		@JsonProperty("job")
		private Object job;
	}
}
