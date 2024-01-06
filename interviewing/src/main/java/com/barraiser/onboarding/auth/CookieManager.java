/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth;

import com.barraiser.onboarding.auth.pojo.AuthTokens;
import com.barraiser.onboarding.common.ApplicationProfile;
import com.barraiser.onboarding.common.StaticAppConfigValues;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class CookieManager {
	private final String FEATURE_TOGGLE_COOKIE_NAME = "ft";
	private final String FIREBASE_AUTH_COOKIE_NAME = "fb";
	private final Integer NO_OF_SECONDS_IN_A_YEAR = 365 * 24 * 60 * 60;
	private final String COOKIE_PATH_FOR_ALL_ROUTES = "/";

	private final StaticAppConfigValues staticAppConfigValues;
	private final Environment environment;

	public ResponseCookie.ResponseCookieBuilder getBarRaiserCookie(final String name, final String value) {
		ResponseCookie.ResponseCookieBuilder cookie = ResponseCookie.from(name, value)
				.httpOnly(true)
				.maxAge(this.NO_OF_SECONDS_IN_A_YEAR)
				.path(this.COOKIE_PATH_FOR_ALL_ROUTES);
		if (this.environment.getActiveProfiles()[0].equals(ApplicationProfile.PROD)) {
			cookie = cookie.domain(this.staticAppConfigValues.getCookieDomain());
		}
		cookie = cookie.secure(true).sameSite("None");
		return cookie;
	}

	public List<ResponseCookie> getSignOutCookies() {
		ResponseCookie.ResponseCookieBuilder refreshTokenCookie = this.getBarRaiserCookie(AuthTokens.REFRESH_TOKEN,
				"invalidated");
		refreshTokenCookie = refreshTokenCookie.maxAge(1);
		ResponseCookie.ResponseCookieBuilder idTokenCookie = this.getBarRaiserCookie(AuthTokens.ID_TOKEN,
				"invalidated");
		idTokenCookie = idTokenCookie.maxAge(1);
		ResponseCookie.ResponseCookieBuilder accessTokenCookie = this.getBarRaiserCookie(AuthTokens.ACCESS_TOKEN,
				"invalidated");
		accessTokenCookie = accessTokenCookie.maxAge(1);

		return List.of(refreshTokenCookie.build(), idTokenCookie.build(), accessTokenCookie.build());
	}

	public ResponseCookie getFeatureToggleCookie(final Map<String, Boolean> featureToggles) {
		final List<String> enabledFeatures = new ArrayList<>();
		featureToggles.forEach((ftName, enabled) -> {
			if (Boolean.TRUE.equals(enabled)) {
				enabledFeatures.add(ftName);
			}
		});
		ResponseCookie.ResponseCookieBuilder cookie = ResponseCookie
				.from(this.FEATURE_TOGGLE_COOKIE_NAME, StringUtils.join(enabledFeatures, "|"))
				.domain(this.staticAppConfigValues.getCookieDomain())
				.path(this.COOKIE_PATH_FOR_ALL_ROUTES);
		return cookie.build();
	}

	public ResponseCookie getFirebaseAuthCookie(final String token) {
		ResponseCookie.ResponseCookieBuilder cookie = ResponseCookie.from(this.FIREBASE_AUTH_COOKIE_NAME, token)
				.domain(this.staticAppConfigValues.getCookieDomain())
				.path(this.COOKIE_PATH_FOR_ALL_ROUTES);
		return cookie.build();
	}
}
