/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.input;

import com.barraiser.common.graphql.types.QuestionType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CategoricalQuestionInput {

	private List<String> categoryIds;

	private QuestionType questionType;

	private String question;

	private Boolean isPreInterviewQuestion;
}
