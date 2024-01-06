/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.UserRole;

public interface ResourceAuthorizer<T> {
	String type();

	void can(final AuthenticatedUser user, final String action, final T resource)
			throws AuthorizationException;

	default boolean isSuperUser(final AuthenticatedUser user) {
		return user.getRoles().contains(UserRole.OPS) || user.getRoles().contains(UserRole.ADMIN);
	}
}
