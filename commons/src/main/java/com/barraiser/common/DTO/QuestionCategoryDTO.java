/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class QuestionCategoryDTO {
	private String id;

	private String name;
}
