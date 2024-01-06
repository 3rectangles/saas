/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.common;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.barraiser.onboarding.common.Constants.*;

@Log4j2
@Component
@Order(RequestFilterPriority.contextPopulation)
@AllArgsConstructor
public class DownstreamContextPopulationFilter implements Filter {

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
			throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) servletRequest;
		final HttpServletResponse response = (HttpServletResponse) servletResponse;

		/**
		 * TODO: We can add code to extract partnerid, partnership model etc also at
		 * this level, so it always be available for use downstream for each request.
		 */
		try {
			final Map<String, Object> context = new HashMap<>();

			context.put(CONTEXT_KEY_USER_AGENT, request.getHeader("User-Agent"));
			context.put(CONTEXT_KEY_SOURCE_IP, request.getHeader("X-Forwarded-For"));

			request.setAttribute("context", context);
		} catch (Exception e) {
			log.error("ERROR exception while constructing downstream context for request {},{},{}", request, e, e);
		}

		filterChain.doFilter(request, servletResponse);
	}
}
