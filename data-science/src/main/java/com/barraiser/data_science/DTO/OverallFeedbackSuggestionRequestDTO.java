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
public class OverallFeedbackSuggestionRequestDTO {
	@JsonProperty("question")
	private String question;

	@JsonProperty("feedback")
	private String feedback;

	@JsonProperty("rating")
	private Integer rating;

	@JsonProperty("difficulty")
	private String difficulty;
}
