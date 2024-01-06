/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CancellationPredictionRequest {
	@JsonProperty("interview")
	private InterviewData interview;

	@JsonProperty("slots")
	private List<Slot> slots;

	@Builder(toBuilder = true)
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class InterviewData {
		@JsonProperty("interview_round")
		private String interviewRound;

		@JsonProperty("target_company_id")
		private String targetCompanyId;

		@JsonProperty("category")
		private String category;

		@JsonProperty("domain_id")
		private String domainId;

		@JsonProperty("round_number")
		private Integer roundNumber;

		@JsonProperty("duration")
		private Integer duration;
	}

	@Builder(toBuilder = true)
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class Slot {
		@JsonProperty("start_time")
		private Long startTime;
	}
}
