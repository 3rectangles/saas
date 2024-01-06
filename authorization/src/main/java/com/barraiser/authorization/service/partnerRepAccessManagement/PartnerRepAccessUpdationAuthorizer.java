/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.service.partnerRepAccessManagement;

import com.barraiser.authorization.DTO.AuthzDTO;
import com.barraiser.authorization.service.GlobalRolesAuthorizer;
import com.barraiser.authorization.service.ResourceAuthorizer;
import com.barraiser.authorization.service.UserPermissionManager;
import com.barraiser.commons.auth.Action;
import com.barraiser.commons.auth.AuthorizationInput;
import com.barraiser.commons.auth.AuthorizationResult;
import com.barraiser.commons.auth.Resource;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class PartnerRepAccessUpdationAuthorizer implements ResourceAuthorizer {

	private GlobalRolesAuthorizer globalRolesAuthorizer;
	private UserPermissionManager userPermissionManager;

	@Override
	public AuthorizationResult authorize(AuthorizationInput authorizationInput) {

		final String partnerId = (String) authorizationInput.getEnvironment().get("partnerId");

		final AuthzDTO globalRoleAuthorizationResult = this.globalRolesAuthorizer
				.authorizeForGlobalRoles(authorizationInput);

		if (Boolean.TRUE.equals(globalRoleAuthorizationResult.getIsSuperUser())) {
			return globalRoleAuthorizationResult.getAuthorizationResult();
		}

		if (this.userPermissionManager.isSuperAdminForPartner(authorizationInput.getAuthenticatedUser().getUserName(),
				partnerId)) {
			return AuthorizationResult.builder()
					.isAuthorized(Boolean.TRUE)
					.build();
		}

		return AuthorizationResult.builder()
				.isAuthorized(Boolean.FALSE)
				.build();

	}

	@Override
	public Action action() {
		return Action.WRITE;
	}

	@Override
	public Resource resource() {
		return Resource.PARTNER_REP;
	}

}
