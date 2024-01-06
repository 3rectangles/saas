/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionCategoryPredictionResponseDTO {
	@JsonProperty("status")
	private String status;

	@JsonProperty("result")
	private Result result;

	@SuperBuilder(toBuilder = true)
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Result {
		@JsonProperty("prediction")
		private Prediction prediction;
	}

	@SuperBuilder(toBuilder = true)
	@Getter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Prediction {
		@JsonProperty("match_score")
		private Integer matchScore;

		@JsonProperty("original_predicted_category")
		private String originalPredictedCategory;

		@JsonProperty("predicted_category")
		private String predictedCategory;
	}
}
