/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.requests;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class QuestionTagRequest {
	private String question;
}
