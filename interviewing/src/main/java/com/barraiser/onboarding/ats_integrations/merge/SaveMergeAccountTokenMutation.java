/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations.merge;

import com.barraiser.ats_integrations.merge.MergeAuthenticationManager;
import com.barraiser.common.graphql.input.SaveMergeAccountTokenInput;
import com.barraiser.onboarding.ats_integrations.publisher.SqsProducer;
import com.barraiser.onboarding.graphql.DataFetcherType;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Log4j2
@AllArgsConstructor
public class SaveMergeAccountTokenMutation implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final MergeAuthenticationManager mergeAuthenticationManager;

	@Override
	public String name() {
		return "saveMergeAccountToken";
	}

	@Override
	public String type() {
		return DataFetcherType.MUTATION.getValue();
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		final SaveMergeAccountTokenInput input = this.graphQLUtil
				.getInput(
						environment,
						SaveMergeAccountTokenInput.class);

		log.info(String.format(
				"Saving merge account token for partnerId:%s",
				input.getPartnerId()));

		this.mergeAuthenticationManager
				.saveMergeAccountToken(input);
		return true;
	}
}
