/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EvaluationScore {
	private Double bgs;
	private Double partnerBGS;
	private Double overallBGS;
	private Double defaultScoringAlgoVersion;
	private List<SkillScore> barraiserScores;
	private List<SkillScore> partnerScores;
	private List<SkillScore> overallScores;
}
