/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science.DTO;

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
public class FeedbackRecommendationResponseDTO {
	@JsonProperty("status")
	private String status;

	@JsonProperty("result")
	private Result result;

	@SuperBuilder(toBuilder = true)
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Result {
		@JsonProperty("predictions")
		private List<Prediction> predictions;
	}

	@SuperBuilder(toBuilder = true)
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Prediction {
		@JsonProperty("text")
		private String text;

		@JsonProperty("original_text")
		private String originalText;
	}
}
