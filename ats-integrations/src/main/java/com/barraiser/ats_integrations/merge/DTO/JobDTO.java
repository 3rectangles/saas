/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.merge.DTO;

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
public class JobDTO {
	@JsonProperty("id")
	private String id;

	@JsonProperty("remote_id")
	private String remoteId;

	@JsonProperty("name")
	private String name;

	@JsonProperty("description")
	private String description;

	@JsonProperty("code")
	private String code;

	@JsonProperty("status")
	private String status;

	@JsonProperty("remote_created_at")
	private String remoteCreatedAt;

	@JsonProperty("remote_updated_at")
	private String remoteUpdatedAt;

	@JsonProperty("confidential")
	private Boolean confidential;

	@JsonProperty("departments")
	private List<String> departments;

	@JsonProperty("offices")
	private List<String> offices;

	@JsonProperty("hiring_managers")
	private List<String> hiringManagers;

	@JsonProperty("recruiters")
	private List<String> recruiters;
}
