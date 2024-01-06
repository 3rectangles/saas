/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.sso;

public interface SSOConfig {
	String source();

	String getClientId();

	String getClientSecret();

	String getTokenEndpoint();

	String getAuthorizationEndpoint();
}
