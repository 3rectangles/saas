/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.recommendation;

import com.barraiser.common.graphql.types.EvaluationRecommendation;
import com.barraiser.onboarding.dal.InterviewDAO;

public interface EvaluationRecommenderStrategy {
	String version();

	EvaluationRecommendation getRecommendation(final String evaluationId);

	EvaluationRecommendation getRecommendation(final InterviewDAO interviewDAO, final String evaluationId);
}
