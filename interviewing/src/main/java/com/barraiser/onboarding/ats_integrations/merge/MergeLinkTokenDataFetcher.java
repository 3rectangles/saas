/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations.merge;

import com.barraiser.ats_integrations.merge.MergeAuthenticationManager;
import com.barraiser.common.graphql.input.GetMergeLinkTokenInput;
import com.barraiser.common.graphql.types.MergeLinkToken;
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
public class MergeLinkTokenDataFetcher implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final MergeAuthenticationManager mergeAuthenticationManager;

	@Override
	public String name() {
		return "getMergeLinkToken";
	}

	@Override
	public String type() {
		return DataFetcherType.QUERY.getValue();
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		final GetMergeLinkTokenInput input = this.graphQLUtil
				.getInput(
						environment,
						GetMergeLinkTokenInput.class);

		log.info(String.format(
				"Getting merge link token for partnerId:%s",
				input.getPartnerId()));

		final MergeLinkToken mergeLinkToken = this.mergeAuthenticationManager
				.getMergeLinkToken(input);

		return DataFetcherResult
				.newResult()
				.data(mergeLinkToken)
				.build();
	}
}
