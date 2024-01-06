/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.graphql;

import com.barraiser.onboarding.auth.CookieManager;
import com.barraiser.commons.auth.AuthenticatedUser;
import graphql.ExecutionResult;
import graphql.GraphQLContext;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

import static com.barraiser.onboarding.common.Constants.EVALUATION_MODULE;
import static com.barraiser.onboarding.graphql.Constants.QUERY_GET_EVALUATIONS;

@Log4j2
@Component
@AllArgsConstructor
public class GraphQLHTTPHandler {
	private final GraphQLQueryExecutor graphQLQueryExecutor;
	private final CookieManager cookieManager;

	public ExecutionResult handle(GraphQLRequest graphQLRequest, HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {
		final GraphQLContext context = this.getContext(httpServletRequest, graphQLRequest.getQuery());

		final ExecutionResult result = this.graphQLQueryExecutor.execute(
				graphQLRequest.getQuery(),
				graphQLRequest.getVariables(),
				context);

		httpServletResponse.addHeader(HttpHeaders.SET_COOKIE,
				this.cookieManager.getFirebaseAuthCookie(null).toString());
		this.contextPostProcess(context, httpServletResponse);

		return result;
	}

	private GraphQLContext getContext(final HttpServletRequest request, final String query) {
		final GraphQLContext context = GraphQLContext.newContext().build();

		// the authenticatedUser will always be instantiated, hence a null check on
		// authenticatedUser's instance is not
		// a sign that user is unauthenticated.
		final AuthenticatedUser authenticatedUser = (AuthenticatedUser) request.getAttribute("loggedInUser");
		if (authenticatedUser != null) {
			context.put(Constants.CONTEXT_KEY_USER, authenticatedUser);
		}

		final String module = this.getModule(query);
		context.put("module", module);
		return context;
	}

	private String getModule(final String query) {
		if (QUERY_GET_EVALUATIONS.equalsIgnoreCase(query)) {
			return EVALUATION_MODULE;
		}
		return "";
	}

	private void contextPostProcess(final GraphQLContext context, final HttpServletResponse response) {
		// set the cookies
		final List<ResponseCookie> cookies = context.get(Constants.CONTEXT_KEY_COOKIES);
		log.info("Setting up the returnable cookies");
		if (cookies != null) {
			for (final ResponseCookie cookie : cookies) {
				response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
			}
		}
	}
}
