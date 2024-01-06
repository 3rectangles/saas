/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.user.auth;

import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.auth.ResourceAuthorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.user.UserInformationManagementHelper;
import com.barraiser.commons.auth.UserRole;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Log4j2
public class UserDetailsAuthorizer implements ResourceAuthorizer {
	public final static String RESOURCE_TYPE = "user_details";

	public final static String ACTION_READ_EXPERT_DETAILS = "read_expert_details";
	public static final String ACTION_WRITE_USER_DETAILS = "write_user_details";

	private final UserInformationManagementHelper userInformationManagementHelper;

	@Override
	public String type() {
		return RESOURCE_TYPE;
	}

	@Override
	public void can(AuthenticatedUser user, String action, Object resource) throws AuthorizationException {
		final String userId = resource.toString();
		switch (action) {
			case ACTION_READ_EXPERT_DETAILS:
				this.canReadExpertDetails(user, userId);
				break;
			default:
				throw new IllegalArgumentException(
						"no valid action found " + action + " on resource type " + RESOURCE_TYPE);
		}
	}

	private void canReadExpertDetails(final AuthenticatedUser user, final String expertId) {
		final List<String> roles = this.userInformationManagementHelper.getRolesOfUser(expertId);
		if (!roles.contains(UserRole.EXPERT.getRole())) {
			throw new AuthorizationException();
		}

		if (this.isSuperUser(user)) {
			return;
		}

		if (!user.getUserName().equals(expertId)) {
			throw new AuthorizationException();
		}
	}
}
