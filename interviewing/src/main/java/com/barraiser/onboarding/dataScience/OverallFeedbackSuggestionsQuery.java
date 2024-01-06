/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dataScience;

import com.barraiser.common.graphql.input.GetOverallFeedbackSuggestionsInput;
import com.barraiser.common.graphql.types.OverallFeedbackSuggestions;
import com.barraiser.onboarding.graphql.GraphQLQuery;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@AllArgsConstructor
public class OverallFeedbackSuggestionsQuery implements GraphQLQuery {
	private final GraphQLUtil graphQLUtil;
	private final DataScienceFeignClient dataScienceFeignClient;

	@Override
	public String name() {
		return "getOverallFeedbackSuggestions";
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		final GetOverallFeedbackSuggestionsInput input = this.graphQLUtil
				.getInput(
						environment,
						GetOverallFeedbackSuggestionsInput.class);

		log.info(String.format(
				"Fetching overall feedback suggestions for interviewId:%s",
				input.getInterviewId()));

		final OverallFeedbackSuggestions overallFeedbackSuggestions = this.dataScienceFeignClient
				.generateOverallFeedbackSuggestions(input)
				.getBody();

		return DataFetcherResult
				.newResult()
				.data(overallFeedbackSuggestions)
				.build();
	}
}
