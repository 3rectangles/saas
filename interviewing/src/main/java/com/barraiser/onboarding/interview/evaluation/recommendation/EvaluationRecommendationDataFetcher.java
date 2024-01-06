/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.recommendation;

import com.barraiser.common.graphql.types.Evaluation;
import com.barraiser.onboarding.graphql.GraphQLQuery;
import com.barraiser.onboarding.interview.evaluation.scores.EvaluationScoreCriteria;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EvaluationRecommendationDataFetcher implements GraphQLQuery {
	@Override
	public String name() {
		return "recommendation";
	}

	@Override
	public String type() {
		return "Evaluation";
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final Evaluation evaluation = environment.getSource();
		return environment
				.getDataLoader(EvaluationRecommendationDataLoaderFactory.DATA_LOADER_NAME)
				.load(EvaluationRecommendationCriteria.builder()
						.evaluationId(evaluation.getId())
						.recommendationVersion(evaluation.getDefaultRecommendationVersion())
						.build());
	}
}
