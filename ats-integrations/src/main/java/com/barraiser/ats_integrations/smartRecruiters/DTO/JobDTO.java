/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.smartRecruiters.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JobDTO {
	@JsonProperty("id")
	private String id;

	@JsonProperty("title")
	private String title;

	@JsonProperty("refNumber")
	private String refNumber;

	@JsonProperty("createdOn")
	private String createdOn;

	@JsonProperty("updatedOn")
	private String updatedOn;

	@JsonProperty("lastActivityOn")
	private String lastActivityOn;

	@JsonProperty("department")
	private Object department;

	@JsonProperty("location")
	private Object location;

	@JsonProperty("status")
	private String status;

	@JsonProperty("postingStatus")
	private String postingStatus;

	@JsonProperty("language")
	private Object language;

	@JsonProperty("actions")
	private Map<String, Object> actions;
}
