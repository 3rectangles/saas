/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.DefaultQuestionsRepository;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.MultiParentTypeDataFetcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class ExcerptDataFetcher implements MultiParentTypeDataFetcher {
	private final ObjectMapper objectMapper;
	private final GraphQLUtil graphQLUtil;
	private final DefaultQuestionsRepository defaultQuestionsRepository;

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of("InterviewStructure", "excerpts"),
				List.of("Query", "getExcerpts"));
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {

		// TBD: Remove this class as we don't use excerpts anymore
		return DataFetcherResult.newResult()
				.data(null)
				.build();
	}
}
