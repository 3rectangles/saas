/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores;

import com.barraiser.common.graphql.input.CategoryCutoffInput;
import com.barraiser.common.graphql.types.SkillScore;
import com.barraiser.onboarding.dal.EvaluationScoreDAO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
public class BgsCalculator {
	final ScoreScaleConverter scoreScaleConverter;

	public double calculateBgs(final List<SkillScore> scores, String partnerID) {
		if (scores.isEmpty()) {
			return 0;
		}
		Double totalWeightage = 0.0;
		Double weightedScore = 0.0;
		for (final SkillScore score : scores) {
			totalWeightage += score.getWeightage();
			weightedScore += (score.getScore() * score.getWeightage());
		}
		double bgs800 = weightedScore / totalWeightage;
		return scoreScaleConverter.convertScoreFrom800(bgs800, partnerID);
	}

	public static double calculateBgsNoScaleDouble(final List<SkillScore> scores) {
		if (scores.isEmpty()) {
			return 0D;
		}
		Double totalWeightage = 0.0;
		Double weightedScore = 0.0;
		for (final SkillScore score : scores) {
			totalWeightage += score.getWeightage();
			weightedScore += (score.getScore() * score.getWeightage());
		}
		return weightedScore / totalWeightage;
	}

	public static int calculateBgsNoScale(final List<SkillScore> scores) {
		if (scores.isEmpty()) {
			return 0;
		}
		Double totalWeightage = 0.0;
		Double weightedScore = 0.0;
		for (final SkillScore score : scores) {
			totalWeightage += score.getWeightage();
			weightedScore += (score.getScore() * score.getWeightage());
		}
		// todo: change overall score calculation
		return (int) Math.ceil(weightedScore / totalWeightage);
	}

	public static int calculateBgsWithDAO(final List<EvaluationScoreDAO> scores) {
		if (scores == null || scores.isEmpty()) {
			return 0;
		}
		Double totalWeightage = 0.0;
		Double weightedScore = 0.0;
		for (final EvaluationScoreDAO score : scores) {
			totalWeightage += score.getWeightage();
			weightedScore += (score.getScore() * score.getWeightage());
		}
		// todo: change overall score calculation
		return (int) Math.ceil(weightedScore / totalWeightage);
	}

	public static boolean checkSkillCutoff(final List<SkillScore> scores, List<CategoryCutoffInput> categoryCutoffs) {
		if (scores.isEmpty()) {
			return false;
		}
		for (final SkillScore score : scores) {
			for (final CategoryCutoffInput categoryCutoff : categoryCutoffs) {
				if (score.getSkillId().equals(categoryCutoff.getCategoryId())) {
					if (score.getScore() != null && categoryCutoff.getCutoffScore() != null) {
						if (score.getScore() < categoryCutoff.getCutoffScore())
							return false;
					}
				}
			}
		}
		return true;
	}
}
