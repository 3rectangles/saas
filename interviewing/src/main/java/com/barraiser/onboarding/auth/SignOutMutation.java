/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth;

import com.barraiser.onboarding.featureToggle.FeatureToggleManager;
import com.barraiser.onboarding.graphql.Constants;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.types.SignoutResult;
import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
public class SignOutMutation implements NamedDataFetcher<SignoutResult> {
	private final CookieManager cookieManager;
	final private FeatureToggleManager featureToggleManager;

	@Override
	public String name() {
		return "signOut";
	}

	@Override
	public String type() {
		return MUTATION_TYPE;
	}

	@Override
	public SignoutResult get(final DataFetchingEnvironment environment) throws Exception {
		final GraphQLContext context = environment.getContext();

		final List<ResponseCookie> cookies = new ArrayList<>();
		cookies.addAll(this.cookieManager.getSignOutCookies());
		cookies.add(
				this.cookieManager.getFeatureToggleCookie(this.featureToggleManager.getFeatureTogglesForUser(null)));
		context.put(Constants.CONTEXT_KEY_COOKIES, cookies);

		return SignoutResult.builder()
				.success(true)
				.build();
	}
}
