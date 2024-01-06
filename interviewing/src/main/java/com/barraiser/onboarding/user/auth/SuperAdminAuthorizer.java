/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user.auth;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.commons.auth.UserRole;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Log4j2
public class SuperAdminAuthorizer {

	public Boolean isSuperAdmin(final AuthenticatedUser authenticatedUser) {
		return authenticatedUser.getRoles().contains(UserRole.ADMIN) ||
				authenticatedUser.getRoles().contains(UserRole.OPS);
	}

}
