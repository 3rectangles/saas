/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import java.util.List;

import com.barraiser.common.dal.Money;
import com.barraiser.common.graphql.input.CategoryCutoffInput;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class RoundLevelInterviewStructure {
	private String atsId;
	private String id;
	private String interviewRound;
	private InterviewStructure interviewStructure;
	private Integer cutOffScore;
	private Integer thresholdScore;
	private Integer interviewCutoffScore;
	private List<CategoryCutoffInput> categoryCutoffs;
	private Boolean requiresApproval;
	private String problemStatementLink;
	private Money price;
	private Double margin;
	private Integer recommendationScore;
}
