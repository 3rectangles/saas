/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.graphql.directives;

import static com.barraiser.onboarding.graphql.Constants.CONTEXT_KEY_USER;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.UserRole;
import graphql.GraphQLContext;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLFieldsContainer;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class AuthorizationDirective implements SchemaDirectiveWiring {
	/**
	 * Note: This method gets called only once in application lifecycle. On each
	 * graphql request,
	 * the @{@link DataFetcher#get} method is invoked.
	 *
	 * <p>
	 * Rules : If no role is mentioned, only admin and ops can access the field.
	 */
	@Override
	public GraphQLFieldDefinition onField(
			final SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {

		final GraphQLFieldDefinition field = environment.getElement();
		final GraphQLFieldsContainer parentType = environment.getFieldsContainer();
		final DataFetcher originalDataFetcher = environment.getCodeRegistry().getDataFetcher(parentType, field);

		final List<String> targetAuthRolesArray = new ArrayList<>();

		if (environment.getDirective().getArgument("roles") != null &&
				environment.getDirective().getArgument("roles").getValue() != null) {
			((List<String>) environment.getDirective().getArgument("roles").getValue()).stream().forEach(r -> {
				targetAuthRolesArray.add(r.toLowerCase());
			});
		} else {
			Arrays.stream(UserRole.values()).forEach(u -> {
				targetAuthRolesArray.add(u.getRole());
			});

		}
		environment
				.getCodeRegistry()
				.dataFetcher(
						parentType, field,
						env -> getResultForAuthorization(targetAuthRolesArray, originalDataFetcher, env));
		return field;
	}

	private Object getResultForAuthorization(
			final List<String> targetAuthRolesArray,
			final DataFetcher originalDataFetcher,
			final DataFetchingEnvironment dataFetchingEnvironment)
			throws Exception {
		final AuthenticatedUser authenticatedUser = ((GraphQLContext) dataFetchingEnvironment.getContext())
				.get(CONTEXT_KEY_USER);
		if (authenticatedUser == null) {
			log.warn("no auth user found");
			return null;
		}

		if (isAuthorized(authenticatedUser, targetAuthRolesArray)) {
			return originalDataFetcher.get(dataFetchingEnvironment);
		}
		return null;
	}

	private boolean isAuthorized(
			final AuthenticatedUser authenticatedUser, final List<String> targetAuthRolesArray) {

		if (authenticatedUser.getRoles().contains(UserRole.ADMIN)
				|| authenticatedUser.getRoles().contains(UserRole.OPS)) {
			return true;
		}

		final Optional<UserRole> userHasDesiredRole = targetAuthRolesArray.stream()
				.map(UserRole::fromString)
				.filter(x -> authenticatedUser.getRoles().contains(x))
				.findAny();

		return userHasDesiredRole.isPresent();
	}
}
