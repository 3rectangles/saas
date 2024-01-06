/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.recommendation;

import com.barraiser.common.graphql.types.EvaluationRecommendation;
import com.barraiser.onboarding.dal.EvaluationRecommendationDAO;
import org.springframework.stereotype.Component;

@Component
public class EvaluationRecommendationMapper {
	public EvaluationRecommendation toEvaluationRecommendation(
			final EvaluationRecommendationDAO evaluationRecommendationDAO) {
		return EvaluationRecommendation
				.builder()
				.recommendationType(evaluationRecommendationDAO.getRecommendationType())
				.screeningCutOff(evaluationRecommendationDAO.getScreeningCutOff())
				.build();
	}
}
