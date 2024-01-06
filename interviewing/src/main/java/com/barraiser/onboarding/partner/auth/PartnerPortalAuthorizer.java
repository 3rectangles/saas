/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.partner.auth;

import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.auth.ResourceAuthorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.PartnerRepsDAO;
import com.barraiser.onboarding.dal.PartnerRepsRepository;
import com.barraiser.commons.auth.UserRole;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
@Log4j2
public class PartnerPortalAuthorizer implements ResourceAuthorizer {
	public final static String RESOURCE_TYPE = "partner_portal";

	// all actions which can be performed to this partner portal resource
	public final static String ACTION_READ_AND_WRITE = "read_and_write_partner_portal";
	public final static String ACTION_READ = "read_partner_portal";

	private final PartnerRepsRepository partnerRepsRepository;

	@Override
	public String type() {
		return RESOURCE_TYPE;
	}

	@Override
	public void can(AuthenticatedUser user, String action, Object resource) throws AuthorizationException {
		final String partnerId = this.getPartnerIdFromResourceObject(resource);
		switch (action) {
			case ACTION_READ_AND_WRITE:
				this.canReadAndWrite(user, partnerId);
				break;
			case ACTION_READ:
				this.canRead(user, partnerId);
				break;
			default:
				throw new IllegalArgumentException(
						"no valid action found " + action + " on resource type " + RESOURCE_TYPE);
		}
	}

	private String getPartnerIdFromResourceObject(final Object resource) {
		return resource.toString();
	}

	public void canReadAndWrite(final AuthenticatedUser user, final String partnerId)
			throws AuthorizationException {

		if (this.isSuperUser(user)) {
			return;
		}

		if (!this.isPartnerRep(user, partnerId)) {
			throw new AuthorizationException();
		}
	}

	public void canRead(final AuthenticatedUser user, final String partnerId)
			throws AuthorizationException {

		if (this.isSuperUser(user)) {
			return;
		}

		if (!this.isPartnerRep(user, partnerId)) {
			throw new AuthorizationException();
		}
	}

	public boolean isPartnerRep(final AuthenticatedUser user, final String partnerId) {
		final Optional<PartnerRepsDAO> partnerRep = this.partnerRepsRepository
				.findByPartnerRepIdAndPartnerId(user.getUserName(), partnerId);
		return partnerRep.isPresent() && user.getRoles().contains(UserRole.PARTNER);
	}

}
