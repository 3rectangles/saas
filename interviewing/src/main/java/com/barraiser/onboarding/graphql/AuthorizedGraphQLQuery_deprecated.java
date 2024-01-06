/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.graphql;

import com.barraiser.common.utilities.ObjectFieldsFilter;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public abstract class AuthorizedGraphQLQuery_deprecated<T> implements DataFetcher<DataFetcherResult<T>> {
	private final GraphQLAbacAuthorizer abacAuthorizer;
	private final ObjectFieldsFilter<T> objectFieldsFilter;

	@Override
	public DataFetcherResult<T> get(final DataFetchingEnvironment environment) throws Exception {
		final AuthorizationResult result = this.abacAuthorizer.authorize(environment);

		final T t = this.fetch(environment, result);

		this.objectFieldsFilter.filter(t, result.getReadableFields());

		return (DataFetcherResult<T>) DataFetcherResult.newResult().data(t).build();
	}

	/**
	 * This is where your actual data fetching code goes in.
	 */
	protected abstract T fetch(final DataFetchingEnvironment environment, AuthorizationResult authorizationResult);

	public abstract List<List<String>> typeNameMap();
}
