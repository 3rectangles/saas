/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.apikey;

import com.barraiser.common.security.DataSecurityManager;
import com.barraiser.onboarding.auth.AuthorizationResourceDTO;
import com.barraiser.onboarding.auth.Authorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.graphql.DataFetcherType;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Log4j2
@AllArgsConstructor
public class ApiKeyDataFetcher implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final ApiKeyRepository apiKeyRepository;
	private final Authorizer authorizer;

	private final DataSecurityManager dataSecurityManager;

	@Override
	public String name() {
		return "getApiKey";
	}

	@Override
	public String type() {
		return DataFetcherType.QUERY.getValue();
	}

	@Override
	public Object get(DataFetchingEnvironment environment) throws Exception {
		final IssueApiKeyInput input = this.graphQLUtil
				.getInput(
						environment,
						IssueApiKeyInput.class);

		final AuthenticatedUser authenticatedUser = this.graphQLUtil
				.getLoggedInUser(environment);

		this.authorizer.can(
				authenticatedUser,
				ApiKeyAuthorizer.ACTION_FETCH_API_KEY,
				AuthorizationResourceDTO
						.builder()
						.type(ApiKeyAuthorizer.RESOURCE_TYPE)
						.build());

		Optional<ApiKeyDAO> apiKeyDAOOptional = this.apiKeyRepository
				.findByKeyNameAndPartnerIdAndDisabledOnIsNull(
						input.getKeyName(),
						input.getPartnerId());

		return apiKeyDAOOptional.map(apiKeyDAO -> IssueApiKeyOutput
				.builder()
				.apiKey(apiKeyDAO.getKey())
				.build()).orElse(null);
	}
}
