/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.graphql;

import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.commons.auth.AuthorizationInput;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.feign.AuthorizationServiceFeignClient;
import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.interview.evaluation.search.auth.AuthorizationInputConstructor;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.util.List;

@AllArgsConstructor
public abstract class AuthorizedGraphQLQuery<T> implements DataFetcher<DataFetcherResult<T>> {
	private final AuthorizationServiceFeignClient authorizationServiceFeignClient;
	private final AuthorizationInputConstructor authorizationInputConstructor;
	private final ObjectFieldsFilter<T> objectFieldsFilter;

	public static final String AUTHORIZATION_ERROR_MESSAGE = "User is not authorized to perform this action. Please contact the admin or write to us on 'support@barraiser.com'";

	@Override
	public DataFetcherResult<T> get(final DataFetchingEnvironment environment) throws Exception {

		final AuthorizationInput authorizationInput = this.authorizationInputConstructor.construct(environment);

		final AuthorizationResult result = Boolean.TRUE.equals(authorizationInput.getShouldAllowAll())
				? AuthorizationResult.builder().isAuthorized(Boolean.TRUE)
						.readableFields(List.of("*"))
						.writeableFields(List.of("*"))
						.build()
				: this.authorizationServiceFeignClient.authorize(authorizationInput);

		if (!result.getIsAuthorized()) {
			throw new AuthorizationException(AUTHORIZATION_ERROR_MESSAGE);
		}

		final T t = this.fetch(environment, result);

		if (!Boolean.TRUE.equals(authorizationInput.getShouldAllowAll())
				&& authorizationInput.getAction().isSingleEntityReadOperation()) {
			this.objectFieldsFilter.filter(t, result.getReadableFields());
		}

		return (DataFetcherResult<T>) DataFetcherResult.newResult().data(t).build();
	}

	/**
	 * This is where your actual data fetching code goes in.
	 */
	protected abstract T fetch(final DataFetchingEnvironment environment, AuthorizationResult authorizationResult)
			throws IOException;

	public abstract List<List<String>> typeNameMap();
}
