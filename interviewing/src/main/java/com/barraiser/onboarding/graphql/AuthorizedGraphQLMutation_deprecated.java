/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.graphql;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;

import java.io.IOException;

import static com.barraiser.onboarding.graphql.NamedDataFetcher.MUTATION_TYPE;

@AllArgsConstructor
public abstract class AuthorizedGraphQLMutation_deprecated<T> implements DataFetcher<T> {
	private final GraphQLAbacAuthorizer abacAuthorizer;

	@Override
	public T get(final DataFetchingEnvironment environment) throws Exception {
		final AuthorizationResult result = this.abacAuthorizer.authorize(environment);

		// TBD:Add filtering of protected fields and test.
		return this.fetch(environment, result);
	}

	protected abstract T fetch(final DataFetchingEnvironment environment, AuthorizationResult authorizationResult)
			throws Exception;

	public abstract String name();

	public String type() {
		return MUTATION_TYPE;
	}

}
