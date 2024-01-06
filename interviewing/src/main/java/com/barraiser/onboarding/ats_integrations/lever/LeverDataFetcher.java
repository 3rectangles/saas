/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations.lever;

import com.barraiser.ats_integrations.lever.LeverDataHandler;
import com.barraiser.common.graphql.input.GetLeverDataInput;
import com.barraiser.common.graphql.types.Lever;
import com.barraiser.onboarding.graphql.DataFetcherType;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@AllArgsConstructor
public class LeverDataFetcher implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final LeverDataHandler leverDataHandler;

	@Override
	public String name() {
		return "getLeverData";
	}

	@Override
	public String type() {
		return DataFetcherType.QUERY.getValue();
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		final GetLeverDataInput input = this.graphQLUtil
				.getInput(
						environment,
						GetLeverDataInput.class);

		log.info(String.format(
				"Fetch lever Postings for partnerId %s",
				input.getPartnerId()));

		Lever result;

		result = this.leverDataHandler.getLeverData(input);

		return DataFetcherResult
				.newResult()
				.data(result)
				.build();
	}
}
