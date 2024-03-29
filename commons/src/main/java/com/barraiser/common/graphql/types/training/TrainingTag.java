/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types.training;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TrainingTag {
	private String id;
	private String name;
}
