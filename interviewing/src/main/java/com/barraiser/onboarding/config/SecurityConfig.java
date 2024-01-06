/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.headers()
				.xssProtection()
				.and()
				.contentSecurityPolicy("form-action 'self'");

		http
				.headers()
				.httpStrictTransportSecurity()
				.includeSubDomains(true)
				.maxAgeInSeconds(31536000);
		http.csrf().disable();
	}
}
