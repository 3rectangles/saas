/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.responses;

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
public class CancellationPredictionResponse {

	@JsonProperty("slots")
	private List<SlotProbability> slotProbabilityList;

	@Builder(toBuilder = true)
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class SlotProbability {
		@JsonProperty("start_time")
		private Long startTime;

		@JsonProperty("cancellation_probability")
		private Double cancellationProbability;
	}
}
