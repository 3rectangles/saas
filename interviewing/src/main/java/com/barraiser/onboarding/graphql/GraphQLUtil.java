/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.graphql;

import com.barraiser.onboarding.auth.AuthenticationException;
import com.fasterxml.jackson.databind.ObjectMapper;

import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import com.barraiser.commons.auth.AuthenticatedUser;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;

import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GraphQLUtil {
	private final ObjectMapper objectMapper;

	@SneakyThrows
	public <T> T getArgument(
			final DataFetchingEnvironment environment, final String param, final Class<T> c) {
		final Object input = environment.getArgument(param);
		return (T) this.objectMapper.readValue(this.objectMapper.writeValueAsString(input), c);
	}

	public <T> T getInput(final DataFetchingEnvironment environment, final Class<T> c) {
		return this.getArgument(environment, "input", c);
	}

	public AuthenticatedUser getLoggedInUser(final DataFetchingEnvironment environment) {
		final GraphQLContext context = environment.getContext();

		final AuthenticatedUser authenticatedUser = context.get(Constants.CONTEXT_KEY_USER);
		if (authenticatedUser == null) {
			throw new AuthenticationException("No authenticated user found");
		}

		return authenticatedUser;
	}
}
