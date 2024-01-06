/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth;

import com.barraiser.commons.auth.AuthenticatedUser;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Log4j2
@AllArgsConstructor
public class Authorizer {
	private final List<ResourceAuthorizer> resourceAuthorizers;

	public void can(final AuthenticatedUser user, final String action,
			final AuthorizationResourceDTO authorizationResource)
			throws AuthorizationException {

		for (final ResourceAuthorizer resourceAuthorizer : this.resourceAuthorizers) {
			if (resourceAuthorizer.type().equals(authorizationResource.getType())) {
				resourceAuthorizer.can(user, action, authorizationResource.getResource());
				return;
			}
		}

		throw new IllegalArgumentException(
				"No authorization resource found for type : " + authorizationResource.getType());
	}
}
