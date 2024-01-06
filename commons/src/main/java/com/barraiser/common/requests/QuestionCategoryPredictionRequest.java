/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.requests;

import com.barraiser.common.DTO.QuestionCategoryDTO;
import com.barraiser.common.DTO.QuestionDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionCategoryPredictionRequest {
	private List<QuestionDTO> questions;

	private List<QuestionCategoryDTO> questionCategories;
}
