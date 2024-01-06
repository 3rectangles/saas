/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth;

import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.barraiser.onboarding.auth.pojo.AuthTokens;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.common.RequestFilterPriority;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Component
@Order(RequestFilterPriority.Authentication)
@AllArgsConstructor
public class AuthenticationFilter implements Filter {
	private final AuthenticationManager authenticationManager;
	private final CookieManager cookieManager;

	@Override
	public void doFilter(
			final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {
		final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		final HttpServletResponse httpServletResponse = (HttpServletResponse) response;

		final Optional<AuthenticatedUser> loggedInUser = getLoggedInUser(httpServletRequest, httpServletResponse);
		loggedInUser.ifPresent(x -> request.setAttribute("loggedInUser", loggedInUser.get()));

		chain.doFilter(request, response);
	}

	private Optional<AuthenticatedUser> getLoggedInUser(
			final HttpServletRequest request, final HttpServletResponse response) {
		final List<Cookie> cookieList = request.getCookies() == null ? List.of() : List.of(request.getCookies());
		final Map<String, String> cookies = cookieList.stream()
				.collect(
						Collectors.toMap(
								Cookie::getName, Cookie::getValue, (o1, o2) -> o1));

		Optional<AuthenticatedUser> authenticatedUser = Optional.empty();

		if (cookies.get(AuthTokens.ID_TOKEN) != null) {
			authenticatedUser = getUserFromCookie(cookies.get(AuthTokens.ID_TOKEN));
		}

		if (authenticatedUser.isEmpty()
				&& Strings.isNotBlank(cookies.get(AuthTokens.REFRESH_TOKEN))) {
			try {
				authenticatedUser = performRefreshTokenAuth(
						cookies.get(AuthTokens.ID_TOKEN),
						cookies.get(AuthTokens.REFRESH_TOKEN),
						response);
			} catch (final Exception ex) {
				log.warn("Problem authenticating with refresh token {}", ex);
			}
		}
		if (authenticatedUser.isEmpty() && request.getHeader("Authorization") != null) {
			authenticatedUser = getUserByApiKey(request);
		}
		return authenticatedUser;
	}

	private Optional<AuthenticatedUser> performRefreshTokenAuth(
			final String idToken, final String refreshToken, final HttpServletResponse response)
			throws InvalidJwtException, IOException, GeneralSecurityException {
		final AuthenticationResultType result = this.authenticationManager.authenticateWithRefreshToken(idToken,
				refreshToken);

		response.addHeader(HttpHeaders.SET_COOKIE,
				this.cookieManager.getBarRaiserCookie(AuthTokens.ID_TOKEN, result.getIdToken()).build().toString());
		response.addHeader(HttpHeaders.SET_COOKIE,
				this.cookieManager.getBarRaiserCookie(
						AuthTokens.ACCESS_TOKEN, result.getAccessToken()).build().toString());
		return this.authenticationManager.authenticateWitIdToken(result.getIdToken());
	}

	private Optional<AuthenticatedUser> getUserFromCookie(final String idToken) {
		try {
			return this.authenticationManager.authenticateWitIdToken(idToken);
		} catch (final InvalidJwtException | AuthenticationException exception) {
			log.warn(exception, exception);
		}
		return Optional.empty();
	}

	private Optional<AuthenticatedUser> getUserByApiKey(final HttpServletRequest request) {
		final String apiKey = request.getHeader("Authorization");
		return this.authenticationManager.authenticateWithApiKey(apiKey);
	}
}
