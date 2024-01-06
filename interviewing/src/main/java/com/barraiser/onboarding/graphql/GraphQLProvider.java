/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.graphql;

import com.barraiser.onboarding.common.ClassPathResourceReader;
import com.barraiser.onboarding.graphql.directives.AuthorizationDirective;
import com.barraiser.onboarding.graphql.errorhandling.CustomExceptionHandler;
import com.barraiser.onboarding.graphql.typeResolvers.TypeResolver;
import graphql.GraphQL;
import graphql.analysis.MaxQueryDepthInstrumentation;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.AsyncSerialExecutionStrategy;
import graphql.execution.instrumentation.ChainedInstrumentation;
import graphql.execution.instrumentation.Instrumentation;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.*;
import graphql.schema.visibility.NoIntrospectionGraphqlFieldVisibility;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import static graphql.schema.idl.RuntimeWiring.newRuntimeWiring;

@Log4j2
@Component
@RequiredArgsConstructor
public class GraphQLProvider {
	private static final int MAXIMUM_ALLOWED_GRAPHQL_QUERY_DEPTH = 7;
	private final ClassPathResourceReader classPathResourceReader;
	private final List<NamedDataFetcher> namedDataFetchers;
	private final CustomExceptionHandler customExceptionHandler;
	private final List<MultiParentTypeDataFetcher> multiParentTypeDataFetchers;
	private final List<AuthorizedGraphQLQuery_deprecated> authorizedGraphQLQueriesDeprecated;
	private final List<AuthorizedGraphQLMutation_deprecated> authorizedGraphQLMutationsDeprecated;
	private final List<AuthorizedGraphQLQuery> authorizedGraphQLQueries;
	private final List<AuthorizedGraphQLMutation> authorizedGraphQLMutations;
	private final AuthorizationDirective authorizationDirective;
	private final List<TypeResolver> typeResolvers;

	static public GraphQL graphQL;
	static public GraphQLSchema graphQLSchema;

	@PostConstruct
	public void constructGraphQL() throws IOException {
		final String schema = this.classPathResourceReader.read("schema.graphqls");

		final SchemaParser schemaParser = new SchemaParser();
		final TypeDefinitionRegistry typeDefinitionRegistry = schemaParser.parse(schema);

		RuntimeWiring.Builder runtimeWiringBuilder = newRuntimeWiring();

		// We will retire these over time
		for (final NamedDataFetcher x : this.namedDataFetchers) {

			runtimeWiringBuilder = runtimeWiringBuilder.type(
					TypeRuntimeWiring
							.newTypeWiring(x.type())
							.dataFetcher(x.name(), x));
		}

		for (final MultiParentTypeDataFetcher x : this.multiParentTypeDataFetchers) {
			for (final Object item : x.typeNameMap()) {
				final String type = ((List<String>) item).get(0);
				final String name = ((List<String>) item).get(1);
				runtimeWiringBuilder = runtimeWiringBuilder.type(
						TypeRuntimeWiring
								.newTypeWiring(type)
								.dataFetcher(name, x));
			}
		}

		for (final AuthorizedGraphQLQuery_deprecated x : this.authorizedGraphQLQueriesDeprecated) {
			for (final Object item : x.typeNameMap()) {
				final String type = ((List<String>) item).get(0);
				final String name = ((List<String>) item).get(1);
				runtimeWiringBuilder = runtimeWiringBuilder.type(
						TypeRuntimeWiring
								.newTypeWiring(type)
								.dataFetcher(name, x));
			}
		}

		for (final AuthorizedGraphQLMutation_deprecated x : this.authorizedGraphQLMutationsDeprecated) {
			runtimeWiringBuilder = runtimeWiringBuilder.type(
					TypeRuntimeWiring
							.newTypeWiring(x.type())
							.dataFetcher(x.name(), x));
		}

		for (final AuthorizedGraphQLQuery x : this.authorizedGraphQLQueries) {
			for (final Object item : x.typeNameMap()) {
				final String type = ((List<String>) item).get(0);
				final String name = ((List<String>) item).get(1);
				runtimeWiringBuilder = runtimeWiringBuilder.type(
						TypeRuntimeWiring
								.newTypeWiring(type)
								.dataFetcher(name, x));
			}
		}

		for (final AuthorizedGraphQLMutation x : this.authorizedGraphQLMutations) {
			runtimeWiringBuilder = runtimeWiringBuilder.type(
					TypeRuntimeWiring
							.newTypeWiring(x.type())
							.dataFetcher(x.name(), x));
		}

		for (final TypeResolver typeResolver : this.typeResolvers) {
			runtimeWiringBuilder = runtimeWiringBuilder.type(TypeRuntimeWiring.newTypeWiring(typeResolver.type())
					.typeResolver(typeResolver.getTypeResolver())
					.build());
		}

		// Disable introspection
		runtimeWiringBuilder.fieldVisibility(NoIntrospectionGraphqlFieldVisibility.NO_INTROSPECTION_FIELD_VISIBILITY);

		final RuntimeWiring runtimeWiring = runtimeWiringBuilder
				.directive("auth", this.authorizationDirective)
				.build();

		final SchemaGenerator schemaGenerator = new SchemaGenerator();
		graphQLSchema = schemaGenerator.makeExecutableSchema(typeDefinitionRegistry, runtimeWiring);

		List<Instrumentation> chainedList = new ArrayList<>();
		chainedList.add(new LoggingInstrumentation());
		chainedList.add(new MaxQueryDepthInstrumentation(MAXIMUM_ALLOWED_GRAPHQL_QUERY_DEPTH));
		ChainedInstrumentation chainedInstrumentation = new ChainedInstrumentation(chainedList);

		graphQL = GraphQL.newGraphQL(graphQLSchema)
				.queryExecutionStrategy(new AsyncExecutionStrategy(this.customExceptionHandler))
				.mutationExecutionStrategy(new AsyncSerialExecutionStrategy(this.customExceptionHandler))
				.instrumentation(chainedInstrumentation)
				.build();
	}
}
