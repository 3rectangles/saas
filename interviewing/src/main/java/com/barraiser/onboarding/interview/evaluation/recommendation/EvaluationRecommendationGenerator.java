/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.recommendation;

import com.barraiser.common.graphql.types.EvaluationRecommendation;
import com.barraiser.onboarding.dal.InterviewDAO;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class EvaluationRecommendationGenerator {
	private final List<EvaluationRecommenderStrategy> evaluationRecommenderStrategies;
	private final EvaluationRecommendationService evaluationRecommendationService;

	public void generateEvaluationRecommendationAndSaveToDatabase(final String evaluationId) {
		for (final EvaluationRecommenderStrategy evaluationRecommenderStrategy : evaluationRecommenderStrategies) {
			final EvaluationRecommendation evaluationRecommendation = this.getEvaluationRecommendation(
					evaluationRecommenderStrategy,
					evaluationId);

			if (evaluationRecommendation != null) {
				this.evaluationRecommendationService.saveEvaluationRecommendation(
						evaluationId,
						evaluationRecommendation, evaluationRecommenderStrategy.version());
			}
		}
	}

	private EvaluationRecommendation getEvaluationRecommendation(
			final EvaluationRecommenderStrategy evaluationRecommenderStrategy,
			final String evaluationId) {
		return evaluationRecommenderStrategy
				.getRecommendation(evaluationId);
	}

	// todo: evaluationID is not used
	public void generateEvaluationRecommendationAndSaveToDatabase(final InterviewDAO interviewDAO,
			final String evaluationId) {
		for (final EvaluationRecommenderStrategy evaluationRecommenderStrategy : evaluationRecommenderStrategies) {
			final EvaluationRecommendation evaluationRecommendation = evaluationRecommenderStrategy
					.getRecommendation(interviewDAO, interviewDAO.getEvaluationId());

			if (evaluationRecommendation != null) {
				this.evaluationRecommendationService.saveEvaluationRecommendation(
						interviewDAO.getEvaluationId(),
						evaluationRecommendation, evaluationRecommenderStrategy.version());
			}
		}
	}
}
