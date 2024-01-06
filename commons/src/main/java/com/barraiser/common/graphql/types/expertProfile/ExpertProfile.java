/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types.expertProfile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
public class ExpertProfile {
	private String expertId;

	private ExpertInterviewingConfiguration interviewingConfiguration;
	private Double minPrice;
}
