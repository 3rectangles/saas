/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.featureToggle;

import com.barraiser.onboarding.auth.CookieManager;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.common.RequestFilterPriority;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@Log4j2
@Component
@Order(RequestFilterPriority.FeatureToggles)
@AllArgsConstructor
public class FeatureTogglesFilter implements Filter {
	final private FeatureToggleManager featureToggleManager;
	final private CookieManager cookieManager;

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) servletRequest;
		final HttpServletResponse response = (HttpServletResponse) servletResponse;

		final AuthenticatedUser authenticatedUser = (AuthenticatedUser) request.getAttribute("loggedInUser");

		try {
			final Map<String, Boolean> featureToggles = this.featureToggleManager
					.getFeatureTogglesForUser(authenticatedUser);
			response.addHeader(HttpHeaders.SET_COOKIE,
					this.cookieManager.getFeatureToggleCookie(featureToggles).toString());
		} catch (final Exception e) {
			log.error(e, e);
		}

		filterChain.doFilter(request, servletResponse);
	}
}
