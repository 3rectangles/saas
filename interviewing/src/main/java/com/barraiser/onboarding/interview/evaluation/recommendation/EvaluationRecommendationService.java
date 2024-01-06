/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.recommendation;

import com.barraiser.common.graphql.types.EvaluationRecommendation;
import com.barraiser.onboarding.dal.EvaluationRecommendationDAO;
import com.barraiser.onboarding.dal.EvaluationRecommendationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.UUID;

@Component
@Log4j2
@AllArgsConstructor
public class EvaluationRecommendationService {
	private final EvaluationRecommendationRepository evaluationRecommendationRepository;

	@Transactional
	public void saveEvaluationRecommendation(
			final String evaluationId,
			final EvaluationRecommendation evaluationRecommendation, final String recommendationVersion) {
		final EvaluationRecommendationDAO evaluationRecommendationDAO = EvaluationRecommendationDAO
				.builder()
				.id(UUID.randomUUID().toString())
				.evaluationId(evaluationId)
				.recommendationAlgoVersion(recommendationVersion)
				.recommendationType(evaluationRecommendation.getRecommendationType())
				.screeningCutOff(evaluationRecommendation.getScreeningCutOff())
				.build();

		this.evaluationRecommendationRepository.deleteByEvaluationIdAndRecommendationAlgoVersion(evaluationId,
				recommendationVersion);
		this.evaluationRecommendationRepository
				.save(evaluationRecommendationDAO);
	}
}
