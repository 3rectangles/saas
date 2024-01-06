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
public class InterviewDTO {
	@JsonProperty("id")
	private String id;

	@JsonProperty("remote_id")
	private String remoteId;

	@JsonProperty("application")
	private String application;

	@JsonProperty("job_interview_stage")
	private String jobInterviewStage;

	@JsonProperty("organizer")
	private String organizer;

	@JsonProperty("interviewers")
	private List<String> interviewers;

	@JsonProperty("location")
	private String location;

	@JsonProperty("start_at")
	private String startAt;

	@JsonProperty("end_at")
	private String endAt;

	@JsonProperty("remote_created_at")
	private String remoteCreatedAt;

	@JsonProperty("remote_updated_at")
	private String remoteUpdatedAt;

	@JsonProperty("status")
	private String status;

	@JsonProperty("remote_data")
	private List<RemoteDataDTO> remoteData;

}
