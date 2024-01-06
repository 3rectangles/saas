/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.service.evaluation;

import com.barraiser.authorization.DTO.AuthzDTO;
import com.barraiser.authorization.service.GlobalRolesAuthorizer;
import com.barraiser.authorization.service.ListResourcePartnerRolesAuthorizer;
import com.barraiser.authorization.service.ResourceAuthorizer;
import com.barraiser.commons.auth.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class SearchEvaluationAuthorizer implements ResourceAuthorizer {

	private GlobalRolesAuthorizer globalRolesAuthorizer;
	private ListResourcePartnerRolesAuthorizer listResourcePartnerRolesAuthorizer;

	@Override
	public AuthorizationResult authorize(AuthorizationInput authorizationInput) {

		final String partnerId = (String) authorizationInput.getEnvironment().get("partnerId");

		final AuthzDTO globalRoleAuthorizationResult = this.globalRolesAuthorizer
				.authorizeForGlobalRoles(authorizationInput);

		if (Boolean.TRUE.equals(globalRoleAuthorizationResult.getIsSuperUser())) {
			return globalRoleAuthorizationResult.getAuthorizationResult();
		}

		final AuthorizationResult partnerLevelAuthorizationResult = this.listResourcePartnerRolesAuthorizer
				.performAuthorizationForPartnerRoles(partnerId,
						authorizationInput);

		return AuthorizationResult.builder()
				.isAuthorized(this.isAuthorized(globalRoleAuthorizationResult, partnerLevelAuthorizationResult))
				.authorizationFilter(partnerLevelAuthorizationResult.getAuthorizationFilter())
				.build();
	}

	private Boolean isAuthorized(final AuthzDTO globalRoleAuthorizationResult,
			final AuthorizationResult partnerLevelAuthorizationResult) {

		if (Boolean.FALSE.equals(globalRoleAuthorizationResult.getAuthorizationResult().getIsAuthorized())
				&& Boolean.FALSE.equals(partnerLevelAuthorizationResult.getIsAuthorized())) {
			return Boolean.FALSE;
		}

		return Boolean.TRUE;
	}

	@Override
	public Action action() {
		return Action.LIST;
	}

	@Override
	public Resource resource() {
		return Resource.EVALUATION;
	}

}
