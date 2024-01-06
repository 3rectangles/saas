/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.graphql;

import com.barraiser.commons.auth.AuthorizationInput;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.interview.evaluation.search.auth.AuthorizationInputConstructor;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;

import static com.barraiser.onboarding.graphql.NamedDataFetcher.MUTATION_TYPE;

@AllArgsConstructor
public abstract class AuthorizedGraphQLMutation<T> implements DataFetcher<T> {

	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final AuthorizationInputConstructor authorizationInputConstructor;

	public static final String AUTHORIZATION_ERROR_MESSAGE = "User is not authorized to perform this action. Please contact the admin or write to us on 'support@barraiser.com'";

	@Override
	public T get(final DataFetchingEnvironment environment) throws Exception {
		final AuthorizationInput input = this.authorizationInputConstructor.construct(environment);
		final AuthorizationResult result = Boolean.TRUE.equals(input.getShouldAllowAll())
				? AuthorizationResult.builder().isAuthorized(Boolean.TRUE)
						.build()
				: this.authorizationServiceFeignClient.authorize(input);

		if (!result.getIsAuthorized()) {
			throw new AuthorizationException(AUTHORIZATION_ERROR_MESSAGE);
		}

		return this.fetch(environment, result);
	}

	protected abstract T fetch(final DataFetchingEnvironment environment, AuthorizationResult authorizationResult)
			throws Exception;

	public abstract String name();

	public String type() {
		return MUTATION_TYPE;
	}

}
