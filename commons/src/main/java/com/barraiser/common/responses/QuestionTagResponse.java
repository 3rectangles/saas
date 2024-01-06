/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class QuestionTagResponse {

	@JsonProperty("question")
	private String question;

	@JsonProperty("question_tags")
	private String questionTag;
}
