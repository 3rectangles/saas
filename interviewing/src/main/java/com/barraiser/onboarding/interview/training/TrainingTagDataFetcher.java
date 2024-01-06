/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.training;

import com.barraiser.common.graphql.input.training.TrainingTagInput;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class TrainingTagDataFetcher implements NamedDataFetcher {

	private final GraphQLUtil graphQLUtil;

	private final TrainingSnippetManager snippetManager;

	@Override
	public String name() {
		return "getAllTrainingTags";
	}

	@Override
	public String type() {
		return QUERY_TYPE;
	}

	@Override
	public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
		final TrainingTagInput input = this.graphQLUtil
				.getInput(
						dataFetchingEnvironment,
						TrainingTagInput.class);
		return snippetManager.getAllSnippetTagByName(input);
	}
}
