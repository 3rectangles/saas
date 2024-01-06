/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class InterviewerRecommendation {
	private Integer hiringRating;

	private String remarks;

	private String cheatingSuspectedRemarks;

	private String interviewIncompleteRemarks;
}
