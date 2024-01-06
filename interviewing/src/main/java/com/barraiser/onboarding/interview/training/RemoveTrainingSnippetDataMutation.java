/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.training;

import com.barraiser.onboarding.auth.Authorizer;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class RemoveTrainingSnippetDataMutation implements NamedDataFetcher {

	private final TrainingSnippetManager snippetManager;

	private final GraphQLUtil graphQLUtil;

	private final Authorizer authorizer;

	@Override
	public String name() {
		return "removeTrainingSnippets";
	}

	@Override
	public String type() {
		return MUTATION_TYPE;
	}

	@Override
	public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
		String snippetId = dataFetchingEnvironment.getArgument("input");
		return snippetManager.removeSnippet(snippetId);
	}
}
