/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.service;

import com.barraiser.commons.auth.AuthorizationInput;
import com.barraiser.commons.auth.AuthorizationResult;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@AllArgsConstructor
@Component
public class AuthorizationService {

	private List<ResourceAuthorizer> resourceAuthorizers;

	public AuthorizationResult authorize(final AuthorizationInput authorizationInput) {

		for (ResourceAuthorizer resourceAuthorizer : this.resourceAuthorizers) {

			if (this.isEligibleToAuthorize(authorizationInput, resourceAuthorizer)) {
				return resourceAuthorizer.authorize(authorizationInput);
			}
		}

		return AuthorizationResult.builder().isAuthorized(Boolean.FALSE).build();
	}

	private Boolean isEligibleToAuthorize(final AuthorizationInput authorizationInput,
			final ResourceAuthorizer resourceAuthorizer) {

		return authorizationInput.getResource().equals(resourceAuthorizer.resource())
				&& authorizationInput.getAction().equals(resourceAuthorizer.action());
	}
}
