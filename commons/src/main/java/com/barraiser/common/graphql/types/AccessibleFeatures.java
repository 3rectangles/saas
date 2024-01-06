/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import com.barraiser.commons.dto.ComponentwiseAccessibleFeatures;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AccessibleFeatures {
	private List<ComponentwiseAccessibleFeatures> accessibleFeatures;
}
