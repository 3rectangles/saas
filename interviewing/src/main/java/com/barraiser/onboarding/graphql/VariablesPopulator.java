/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.graphql;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.communicationUtils.QueryGenerator;
import com.barraiser.commons.auth.UserRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import graphql.ExecutionResult;
import graphql.GraphQLContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Log4j2
// TODO: this class is still in draft. will be used later
public class VariablesPopulator {
	private final GraphQLQueryExecutor graphQLQueryExecutor;

	public void populate() throws JsonProcessingException {
		final List<String> rawVariables = List.of("getEvaluations.candidate.firstName",
				"getEvaluations.jobRole.candidateDisplayName", "getEvaluations.jobRole.company.name",
				"getSchedulingSlots.date");
		final Map<String, List<List<String>>> queryVariables = this.parseRawVariables(rawVariables);

		final Map<String, Object> graphQLVariables = new HashedMap();
		final Map<String, Object> input = new HashedMap();
		input.put("id", "8b73eea8-196d-4faa-ac51-09db91cc00b0s");
		graphQLVariables.put("input", input);

		for (Map.Entry<String, List<List<String>>> queryVariablesEntry : queryVariables.entrySet()) {
			final String queryName = queryVariablesEntry.getKey();
			final List<List<String>> variables = queryVariablesEntry.getValue();
			String query = (new QueryGenerator()).generate(queryName, variables);
			final Map<String, Object> result = this.getQueryResult(query, graphQLVariables);
			final List<Object> values = this.populateVariablesFromResult(variables,
					result.getOrDefault(queryName, new HashedMap()));
		}
	}

	private Map<String, List<List<String>>> parseRawVariables(final List<String> rawVariables) {
		final Map<String, List<List<String>>> queryToVariablesMap = new HashedMap();
		for (final String variable : rawVariables) {
			final List<String> path = List.of(variable.split("\\."));
			final String queryName = path.get(0);
			final List<String> updatedPath = path.subList(1, path.size());
			final List<List<String>> queryVariables = queryToVariablesMap.getOrDefault(queryName, new ArrayList<>());
			queryVariables.add(updatedPath);
			queryToVariablesMap.put(queryName, queryVariables);
		}
		return queryToVariablesMap;
	}

	private Map<String, Object> getQueryResult(final String query, final Map<String, Object> graphQLVariables) {
		final GraphQLContext context = GraphQLContext.newContext().build();
		final AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
				.userName("system")
				.roles(List.of(UserRole.ADMIN))
				.build();
		context.put(Constants.CONTEXT_KEY_USER, authenticatedUser);
		final ExecutionResult result = this.graphQLQueryExecutor.execute(query, graphQLVariables, context);
		return result.getData() != null ? result.getData() : new HashedMap();
	}

	private List<Object> populateVariablesFromResult(final List<List<String>> variables, final Object result) {
		final List<Object> values = new ArrayList<>();
		for (final List<String> variable : variables) {
			Object current = result == null ? new HashedMap() : result;
			for (int i = 0; i < variable.size(); ++i) {
				while (current instanceof ArrayList) {
					current = ((ArrayList<?>) current).get(0);
				}
				current = ((Map<String, Object>) current).getOrDefault(variable.get(i),
						i == variable.size() - 1 ? null : new HashedMap());
				if (i == variable.size() - 1) {
					values.add(current);
				}
			}
		}
		return values;
	}
}
