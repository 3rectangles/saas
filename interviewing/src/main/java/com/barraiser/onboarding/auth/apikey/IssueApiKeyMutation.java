/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.apikey;

import com.barraiser.onboarding.auth.AuthorizationResourceDTO;
import com.barraiser.onboarding.auth.Authorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.graphql.GraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class IssueApiKeyMutation implements GraphQLMutation {
	public static final String ISSUE_API_KEY = "issueApiKey";
	private final GraphQLUtil graphQLUtil;
	private final ApiKeyManager apiKeyManager;
	private final Authorizer authorizer;

	@Override
	public String name() {
		return ISSUE_API_KEY;
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final IssueApiKeyInput input = this.graphQLUtil.getInput(environment, IssueApiKeyInput.class);
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);

		this.authorizer.can(authenticatedUser, ApiKeyAuthorizer.ACTION_ISSUE, AuthorizationResourceDTO.builder()
				.type(ApiKeyAuthorizer.RESOURCE_TYPE)
				.build());
		final String generatedKey = this.apiKeyManager
				.issueApiKey(
						authenticatedUser.getUserName(),
						input.getPartnerId(),
						input.getKeyName(),
						authenticatedUser.getRoles());

		return DataFetcherResult.newResult()
				.data(IssueApiKeyOutput.builder()
						.apiKey(generatedKey)
						.build())
				.build();
	}
}
