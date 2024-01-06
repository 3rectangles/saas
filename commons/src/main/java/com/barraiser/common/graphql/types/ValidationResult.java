/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Getter
@Data
public class ValidationResult {

	private List<FieldValidationResult> fieldErrors;

	private List<FieldValidationResult> fieldWarnings;

	private List<String> overallErrors;

	private List<String> overallWarnings;
}
