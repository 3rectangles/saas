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
public class FeedbackRecommendationRequestDTO {
	@JsonProperty("question")
	private String question;

	@JsonProperty("question_category")
	private String questionCategory;

	@JsonProperty("rating")
	private Integer rating;
}
