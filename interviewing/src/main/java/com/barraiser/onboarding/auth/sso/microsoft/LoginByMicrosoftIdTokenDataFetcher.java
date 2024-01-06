/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.sso.microsoft;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.UrlJwkProvider;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.barraiser.common.utilities.ObjectFieldsFilter;
import com.barraiser.onboarding.auth.AuthenticationManager;
import com.barraiser.onboarding.graphql.AllowAllAuthorizer;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLQuery_deprecated;
import com.barraiser.onboarding.graphql.Constants;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.util.Date;
import java.util.List;

import static com.barraiser.onboarding.graphql.NamedDataFetcher.QUERY_TYPE;

@Component
@Log4j2
public class LoginByMicrosoftIdTokenDataFetcher extends AuthorizedGraphQLQuery_deprecated<Boolean> {
	private final GraphQLUtil graphQLUtil;
	private final AuthenticationManager authenticationManager;

	@Value("${ms-teams.app-id}")
	private String appId;

	public LoginByMicrosoftIdTokenDataFetcher(
			AllowAllAuthorizer authorizer,
			ObjectFieldsFilter<Boolean> objectFieldsFilter,
			GraphQLUtil graphQLUtil,
			AuthenticationManager authenticationManager) {
		super(authorizer, objectFieldsFilter);
		this.graphQLUtil = graphQLUtil;
		this.authenticationManager = authenticationManager;
	}

	@Override
	protected Boolean fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult) {
		final String idToken = this.graphQLUtil.getInput(environment, String.class);
		try {
			DecodedJWT jwt = JWT.decode(idToken);
			this.validateToken(jwt);
			final String email = jwt.getClaims().get("preferred_username").asString();
			final List<ResponseCookie> cookies = this.authenticationManager.authenticateWithOtp(email, "");
			final GraphQLContext context = environment.getContext();
			context.put(Constants.CONTEXT_KEY_COOKIES, cookies);

			return Boolean.TRUE;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void validateToken(final DecodedJWT jwt) {
		try {
			final UrlJwkProvider provider = new UrlJwkProvider(
					new URL("https://login.microsoftonline.com/common/discovery/v2.0/keys"));
			Jwk jwk = provider.get(jwt.getKeyId());
			Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) jwk.getPublicKey(), null);
			algorithm.verify(jwt);

			if (!jwt.getClaims().get("aud").asString().equals(appId)) {
				throw new IllegalArgumentException();
			}
			if (jwt.getExpiresAt().before(new Date())) {
				throw new IllegalArgumentException();
			}
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public List<List<String>> typeNameMap() {
		return List.of(
				List.of(QUERY_TYPE, "loginByMicrosoftIdToken"));
	}
}
