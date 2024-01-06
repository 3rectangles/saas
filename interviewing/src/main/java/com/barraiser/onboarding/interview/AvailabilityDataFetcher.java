/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.graphql.Constants;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AvailabilityDataFetcher implements NamedDataFetcher<DataFetcherResult<Object>> {

	@Override
	public String name() {
		return "availability";
	}

	@Override
	public String type() {
		return Constants.TYPE_INTERVIEWER;
	}

	@Override
	public DataFetcherResult<Object> get(final DataFetchingEnvironment environment) {
		return DataFetcherResult.newResult().build();
	}
}
