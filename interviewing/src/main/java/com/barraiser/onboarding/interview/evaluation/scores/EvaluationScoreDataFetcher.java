/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores;

import com.barraiser.common.graphql.types.Evaluation;
import com.barraiser.onboarding.graphql.GraphQLQuery;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EvaluationScoreDataFetcher implements GraphQLQuery {

	@Override
	public String name() {
		return "evaluationScore";
	}

	@Override
	public String type() {
		return "Evaluation";
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final Evaluation evaluation = environment.getSource();

		return environment
				.getDataLoader(EvaluationScoreDataLoaderFactory.DATA_LOADER_NAME)
				.load(
						EvaluationScoreCriteria.builder()
								.evaluationId(evaluation.getId())
								.scoringAlgo(evaluation.getScoringAlgoVersion())
								.build());
	}
}
