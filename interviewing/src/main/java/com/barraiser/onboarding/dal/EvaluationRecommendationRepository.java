/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationRecommendationRepository
		extends JpaRepository<EvaluationRecommendationDAO, String> {

	Optional<EvaluationRecommendationDAO> findByEvaluationIdAndRecommendationAlgoVersion(String evaluationId,
			String recommendationAlgoVersion);

	List<EvaluationRecommendationDAO> findAllByEvaluationIdIn(List<String> evaluationIdList);

	void deleteByEvaluationIdAndRecommendationAlgoVersion(String evaluationId, String recommendationVersion);
}
