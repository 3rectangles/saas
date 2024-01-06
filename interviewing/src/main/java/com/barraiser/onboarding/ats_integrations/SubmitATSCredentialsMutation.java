/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations;

import com.barraiser.ats_integrations.credentials.ATSCredentialsManager;
import com.barraiser.common.graphql.input.SubmitATSCredentialsInput;
import com.barraiser.onboarding.graphql.DataFetcherType;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@AllArgsConstructor
public class SubmitATSCredentialsMutation implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final ATSCredentialsManager atsCredentialsManager;

	@Override
	public String name() {
		return "submitATSCredentials";
	}

	@Override
	public String type() {
		return DataFetcherType.MUTATION.getValue();
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		final SubmitATSCredentialsInput input = this.graphQLUtil
				.getInput(
						environment,
						SubmitATSCredentialsInput.class);

		log.info(String.format(
				"Submitting ATS Credentials for partnerId:%s atsProvider:%s",
				input.getPartnerId(),
				input.getAtsProvider()));

		this.atsCredentialsManager
				.submitATSCredentials(input);

		return true;
	}
}
