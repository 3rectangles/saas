/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.authorization.service;

import com.barraiser.authorization.DTO.AuthzDTO;
import com.barraiser.commons.auth.AuthorizationInput;
import com.barraiser.commons.auth.AuthorizationResult;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class GlobalRolesAuthorizer {

	private UserPermissionManager userPermissionManager;

	public AuthzDTO authorizeForGlobalRoles(final AuthorizationInput authorizationInput) {
		Boolean isSuperUser = Boolean.FALSE;
		Boolean isAuthorized;

		if (this.userPermissionManager.isGlobalSuperUser(authorizationInput.getAuthenticatedUser().getUserName())) {
			isSuperUser = Boolean.TRUE;
			isAuthorized = Boolean.TRUE;
		} else {
			final List<String> userGlobalRoleIds = this.userPermissionManager
					.getAllGlobalRolesWithRequiredPermissions(authorizationInput);
			isAuthorized = userGlobalRoleIds.size() != 0;
		}

		return AuthzDTO.builder()
				.isSuperUser(isSuperUser)
				.authorizationResult(AuthorizationResult.builder()
						.isAuthorized(isAuthorized)
						.build())
				.build();
	}
}
