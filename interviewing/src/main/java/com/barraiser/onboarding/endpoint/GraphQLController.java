/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.endpoint;

import com.barraiser.onboarding.graphql.GraphQLHTTPHandler;
import com.barraiser.onboarding.graphql.GraphQLRequest;
import com.barraiser.onboarding.graphql.errorhandling.SanitizedError;
import com.barraiser.onboarding.graphql.query_resolution.GraphQLQueryFetcher;
import graphql.ExecutionResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Log4j2
public class GraphQLController {
	private final GraphQLHTTPHandler graphQLHTTPHandler;
	private final GraphQLQueryFetcher queryFetcher;

	@PostMapping(path = "/graphql", consumes = "application/json", produces = "application/json")
	public ResponseEntity<ExecutionResult> getData(@RequestBody final GraphQLRequest graphQLRequest,
			final HttpServletRequest request,
			final HttpServletResponse response) {

		final ExecutionResult result = this.graphQLHTTPHandler.handle(graphQLRequest, request, response);
		return ResponseEntity.ok(result);
	}

	@PostMapping(path = "/data/{queryName}", consumes = "application/json", produces = "application/json")
	public ResponseEntity getData(@PathVariable final String queryName,
			@RequestBody final Map<String, Object> variables,
			final HttpServletRequest request,
			final HttpServletResponse response) {
		log.info("query name {}", queryName);
		final String query = this.queryFetcher.fetch(queryName);

		final GraphQLRequest graphQLRequest = GraphQLRequest.builder()
				.query(query)
				.variables(variables)
				.build();

		final ExecutionResult result = this.graphQLHTTPHandler.handle(graphQLRequest, request, response);

		if (result.getErrors().size() > 0) {
			return formatErrorResponse(result);
		}

		return ResponseEntity.ok(result.getData());
	}

	private ResponseEntity<RestErrorMessage> formatErrorResponse(final ExecutionResult result) {

		final boolean isNotAuthenticated = result.getErrors().stream().anyMatch(x -> {
			final SanitizedError error = (SanitizedError) x;
			return error.getCode() == 401;
		});
		if (isNotAuthenticated) {
			return ResponseEntity.status(401)
					.body(RestErrorMessage.builder()
							.message("You are not authenticated.")
							.build());
		}

		final boolean isNotAuthorized = result.getErrors().stream().anyMatch(x -> {
			final SanitizedError error = (SanitizedError) x;
			return error.getCode() == 403;
		});
		if (isNotAuthorized) {
			return ResponseEntity.status(403)
					.body(RestErrorMessage.builder()
							.message("You are not authorized.")
							.build());
		}

		final boolean badArgument = result.getErrors().stream().anyMatch(x -> {
			final SanitizedError error = (SanitizedError) x;
			return error.getCode() == 400;
		});
		if (badArgument) {
			return ResponseEntity.status(400)
					.body(RestErrorMessage.builder()
							.message("Given input is not valid")
							.build());
		}

		return ResponseEntity.status(500)
				.body(RestErrorMessage.builder()
						.message("Something went wrong")
						.build());

	}
}
