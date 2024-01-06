/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.merge.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JobInterviewStageDTO {
	@JsonProperty("id")
	private String id;

	@JsonProperty("remote_id")
	private String remoteId;

	@JsonProperty("name")
	private String name;

	@JsonProperty("job")
	private String job;

	@JsonProperty("stage_order")
	private Integer stageOrder;

	@JsonProperty("remote_was_deleted")
	private Boolean remoteWasDeleted;

	@JsonProperty("modified_at")
	private String modifiedAt;

	// TODO: add field_mappings and remote_data
}
