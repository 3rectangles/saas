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
public class ApplicationDTO {
	@JsonProperty("id")
	private String id;

	@JsonProperty("remote_id")
	private String remoteId;

	@JsonProperty("candidate")
	private String candidate;

	@JsonProperty("job")
	private String job;

	@JsonProperty("applied_at")
	private String appliedAt;

	@JsonProperty("rejected_at")
	private String rejectedAt;

	@JsonProperty("source")
	private String source;

	@JsonProperty("credited_to")
	private String creditedTo;

	@JsonProperty("current_stage")
	private String currentStage;

	@JsonProperty("reject_reason")
	private String rejectReason;

	@JsonProperty("remote_data")
	private List<RemoteDataDTO> remoteData;
}
