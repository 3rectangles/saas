/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.merge.responses;

import com.barraiser.ats_integrations.merge.DTO.CandidateDTO;
import com.barraiser.ats_integrations.merge.DTO.InterviewDTO;
import com.barraiser.ats_integrations.merge.DTO.JobDTO;
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
public class CandidatesResponse {
	@JsonProperty("next")
	private String next;

	@JsonProperty("previous")
	private String previous;

	@JsonProperty("results")
	private List<CandidateDTO> results;
}
