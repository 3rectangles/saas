/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.notifications;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.barraiser.communication.configurations.SlackConfigurations;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import com.barraiser.onboarding.config.SlackClientConfig;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.input.CreateSlackMemberInput;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Map;

@Log4j2
@Component
@AllArgsConstructor
public class SlackMutation implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final SlackFeignClient slackFeignClient;
	private final AWSSecretsManager awsSecretsManager;
	private final StaticAppConfigValues staticAppConfigValues;
	private final ObjectMapper objectMapper;
	private final SlackClientConfig slackClientConfig;
	private final SlackConfigurations slackConfigurations;

	@Override
	public String name() {
		return "createSlackMember";
	}

	@Override
	public String type() {
		return MUTATION_TYPE;
	}

	@Override
	public Boolean get(final DataFetchingEnvironment environment) throws Exception {
		final CreateSlackMemberInput input = this.graphQLUtil.getInput(environment, CreateSlackMemberInput.class);

		final Map<String, String> slackSecret = this.slackClientConfig.getSlackSecret();

		final SlackBody slackBody = SlackBody.builder()
				.code(input.getCode())
				.client_id(slackSecret.get("client_id"))
				.client_secret(slackSecret.get("client_secret"))
				.redirect_uri(this.staticAppConfigValues.getRedirectURL())
				.build();

		final String response = this.slackFeignClient.getAccessToken(slackBody);

		if (response != null) {
			log.info("Slack Access Token Response: {}", response);
		}

		final SlackResponse slackResponse = this.objectMapper.readValue(response, SlackResponse.class);

		final Boolean createSlackConfiguration = this.slackConfigurations.storeSlackConfigurations(
				input.getState(),
				slackResponse.getSlackWebhook().getChannel(),
				slackResponse.getSlackWebhook().getChannelId(),
				slackResponse.getAccessToken());

		return createSlackConfiguration;
	}
}
