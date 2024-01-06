/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.magicLink;

import com.barraiser.onboarding.auth.AuthenticationManager;
import com.barraiser.onboarding.graphql.Constants;
import com.barraiser.onboarding.graphql.GraphQLQuery;
import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class VerifyMagicLinkDataFetcher implements GraphQLQuery<Boolean> {
	private final MagicLinkManager magicLinkManager;
	private final AuthenticationManager authenticationManager;

	@Override
	public String name() {
		return "verifyMagicLink";
	}

	@Override
	public Boolean get(DataFetchingEnvironment environment) throws Exception {
		final String token = environment.getArgument("token");
		final String email;
		try {
			email = this.magicLinkManager.verifyMagicToken(token);
		} catch (final Exception e) {
			log.warn("invalid magic token : " + e.getMessage(), e);
			return Boolean.FALSE;
		}
		final List<ResponseCookie> cookies = this.authenticationManager.authenticateWithOtp(email, "");
		final GraphQLContext context = environment.getContext();
		context.put(Constants.CONTEXT_KEY_COOKIES, cookies);
		return Boolean.TRUE;
	}
}
