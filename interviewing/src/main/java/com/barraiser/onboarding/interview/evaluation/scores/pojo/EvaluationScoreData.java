/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores.pojo;

import com.barraiser.common.graphql.types.SkillScore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class EvaluationScoreData {
	private List<SkillScore> skillScores;
	private ComputeEvaluationScoresData input;
}
