/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.training;

import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class TrainingSnippetDataFetcher implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final TrainingSnippetManager snippetManager;

	@Override
	public String name() {
		return "getAllTrainingSnippetsForRole";
	}

	@Override
	public String type() {
		return QUERY_TYPE;
	}

	@Override
	public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
		String role = dataFetchingEnvironment.getArgument("input");
		return snippetManager.getAllSnippetByRole(role);
	}
}
