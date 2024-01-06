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
public class QuestionCategoryPredictionRequestDTO {
	@JsonProperty("question")
	private String question;

	@JsonProperty("categories")
	private List<String> categories;
}
