/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import com.barraiser.commons.auth.FilterOperator;
import lombok.*;

import java.util.List;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ApplicableFilter {
	private String fieldName;
	private List<String> dependantFields;
	private List<FilterOperator> operationsPossible;
	private ApplicableFilterType filterType;
	private FieldType fieldType;
	private String displayName;
	private String defaultValue;
}
