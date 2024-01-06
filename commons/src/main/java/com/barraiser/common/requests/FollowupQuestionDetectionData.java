/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.requests;

import com.barraiser.common.DTO.QuestionCategoryDTO;
import com.barraiser.common.DTO.QuestionDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FollowupQuestionDetectionData {
	private QuestionDTO questionDTO;

	private QuestionCategoryDTO questionCategoryDTO;
}
