/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.auth.apikey;

import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.auth.ResourceAuthorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.PartnerRepsDAO;
import com.barraiser.onboarding.dal.PartnerRepsRepository;
import com.barraiser.commons.auth.UserRole;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class ApiKeyAuthorizer implements ResourceAuthorizer<ApiKeyDAO> {
	public final static String RESOURCE_TYPE = "apiKey";

	/**
	 * Only partners, admins & ops are allowed to perform this.
	 */
	public final static String ACTION_ISSUE = "issue";

	public final static String ACTION_FETCH_API_KEY = "fetchApiKey";

	private final PartnerRepsRepository partnerRepsRepository;

	@Override
	public String type() {
		return RESOURCE_TYPE;
	}

	@Override
	public void can(final AuthenticatedUser user, final String action, final ApiKeyDAO resource)
			throws AuthorizationException {
		if (this.isSuperUser(user, action)) {
			return;
		}

		if (ACTION_ISSUE.equals(action)) {
			final boolean isPartner = user.getRoles()
					.stream()
					.anyMatch(x -> x.equals(UserRole.PARTNER));
			if (isPartner) {
				return;
			}
		} else if (ACTION_FETCH_API_KEY.equals(action)) {
			if (this.isPartnerRep(
					user,
					user.getPartnerId())) {
				return;
			}
		}

		throw new AuthorizationException();
	}

	/**
	 * TODO: this method in future should take into consideration that a partner is
	 * also an admin for the partner portal.
	 */
	private boolean isKeyRelatedToPartner(final AuthenticatedUser user, final ApiKeyDAO resource) {
		final boolean isPartner = user.getRoles()
				.stream()
				.anyMatch(x -> x.equals(UserRole.PARTNER));

		return isPartner || !resource.getPartnerId().equals(user.getPartnerId());
	}

	private boolean isSuperUser(final AuthenticatedUser user, final String action) {
		if (ACTION_FETCH_API_KEY.equals(action)) {
			return user.getRoles()
					.stream()
					.anyMatch(x -> x.equals(UserRole.ADMIN));
		}
		return user.getRoles()
				.stream()
				.anyMatch(x -> x.equals(UserRole.ADMIN) || x.equals(UserRole.OPS));
	}

	public boolean isPartnerRep(final AuthenticatedUser user, final String partnerId) {
		final Optional<PartnerRepsDAO> partnerRep = this.partnerRepsRepository
				.findByPartnerRepIdAndPartnerId(user.getUserName(), partnerId);
		return partnerRep.isPresent() && user.getRoles().contains(UserRole.PARTNER);
	}
}
