/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.endpoint;

import com.barraiser.onboarding.interview.evaluation.recommendation.EvaluationRecommendationGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@AllArgsConstructor
public class EvaluationRecommendationController {
	private final EvaluationRecommendationGenerator evaluationRecommendationGenerator;

	@PostMapping(value = "/generate_evaluation_recommendation")
	public void generateEvaluationRecommendation(
			@RequestParam("evaluation_id") final String evaluationId) {
		try {
			this.evaluationRecommendationGenerator
					.generateEvaluationRecommendationAndSaveToDatabase(evaluationId);
		} catch (Exception e) {
			log.error(e, e);
			throw e;
		}
	}
}
