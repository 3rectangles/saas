/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement.JobRoleConfiguration.auth;

import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.auth.ResourceAuthorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.PartnerRepsDAO;
import com.barraiser.onboarding.dal.PartnerRepsRepository;
import com.barraiser.commons.auth.UserRole;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
@Log4j2
public class JobRoleAuthorizer implements ResourceAuthorizer {
	public static final String RESOURCE_TYPE = "job_role";
	private PartnerRepsRepository partnerRepsRepository;

	@Override
	public String type() {
		return RESOURCE_TYPE;
	}

	@Override
	public void can(final AuthenticatedUser user, final String action, final Object resource)
			throws AuthorizationException {
		final String resourceId = this.getJobRoleIdFromResource(resource);
		final String partnerId = this.getPartnerIdFromResource(resource);

		switch (action) {
			case "WRITE":
				this.canWrite(user, partnerId);
				break;
			case "READ":
				this.canRead(user, resourceId);
			default:
				throw new IllegalArgumentException(
						"no valid action found " + action + " on resource type " + RESOURCE_TYPE);
		}
	}

	private void canRead(final AuthenticatedUser user, final String resourceId)
			throws AuthorizationException {
		String partnerId = this.getPartnerIdFromResource(resourceId);
		if (this.isSuperUser(user) || (user.getPartnerId().contains(partnerId))) {
			return;
		}
	}

	private void canWrite(final AuthenticatedUser user, final String partnerId)
			throws AuthorizationException {

		if (this.isSuperUser(user) || isUserBelongsToPartnerId(user, partnerId)) {
			return;
		}
	}

	private boolean isUserBelongsToPartnerId(AuthenticatedUser user, String partnerId) {
		if (partnerId == null) {
			return false;
		}
		List<PartnerRepsDAO> partnerRepsDAOS = partnerRepsRepository.findAllByPartnerRepId(user.getUserName());
		for (PartnerRepsDAO partnerRepsDAO : partnerRepsDAOS) {
			if (partnerRepsDAO.getPartnerId().equals(partnerId)) {
				return true;
			}
		}
		return false;
	}

	private String getJobRoleIdFromResource(final Object resource) {
		return ((Map<String, String>) resource).get("resourceId");
	}

	private String getPartnerIdFromResource(final Object resource) {
		return ((Map<String, String>) resource).get("partnerId");
	}
}
