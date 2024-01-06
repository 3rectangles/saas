/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.ats_integrations;

import com.barraiser.ats_integrations.common.ATSWebhookHandler;
import com.barraiser.common.graphql.input.ActivateATSWebhookInput;
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
public class ActivateATSWebhookMutation implements NamedDataFetcher {
	private final ATSWebhookHandler atsWebhookHandler;
	private final GraphQLUtil graphQLUtil;

	@Override
	public String name() {
		return "activateATSWebhook";
	}

	@Override
	public String type() {
		return DataFetcherType.MUTATION.getValue();
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		final ActivateATSWebhookInput input = this.graphQLUtil
				.getInput(
						environment,
						ActivateATSWebhookInput.class);

		log.info(String.format(
				"Activate ATS Webhook for partnerId:%S and ATSProvdier:%s",
				input.getPartnerId(),
				input.getAtsProvider()));

		this.atsWebhookHandler
				.activateATSWebhook(input);

		return true;
	}
}
