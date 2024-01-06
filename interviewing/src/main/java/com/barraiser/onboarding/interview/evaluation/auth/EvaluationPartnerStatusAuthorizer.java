/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.auth;

import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.auth.ResourceAuthorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.jobrole.JobRoleManager;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import com.barraiser.onboarding.partner.auth.PartnerPortalAuthorizer;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
@Log4j2
public class EvaluationPartnerStatusAuthorizer implements ResourceAuthorizer {
	public final static String RESOURCE_TYPE = "evaluation_partner_status";
	public final static String ACTION_WRITE = "write_evaluation_partner_status";

	private final EvaluationRepository evaluationRepository;
	private final PartnerCompanyRepository partnerCompanyRepository;
	private final StatusRepository statusRepository;
	private final PartnerPortalAuthorizer partnerPortalAuthorizer;
	private final EvaluationStatusManager evaluationStatusManager;
	private final JobRoleManager jobRoleManager;

	@Override
	public String type() {
		return RESOURCE_TYPE;
	}

	@Override
	public void can(final AuthenticatedUser user, final String action, final Object resource)
			throws AuthorizationException {
		final String evaluationId = this.getEvaluationIdFromResourceObject(resource);
		final String partnerStatusId = this.getPartnerStatusIdFromResourceObject(resource);
		switch (action) {
			case ACTION_WRITE:
				this.canWrite(user, evaluationId, partnerStatusId);
				break;
			default:
				throw new IllegalArgumentException(
						"no valid action found " + action + " on resource type " + RESOURCE_TYPE);
		}
	}

	private void canWrite(final AuthenticatedUser user, final String evaluationId, final String partnerStatusId) {
		final String partnerId = this.getPartnerIdOfEvaluation(evaluationId);
		final StatusDAO partnerStatus = this.statusRepository.findById(partnerStatusId).get();
		if (this.evaluationStatusManager.isStatusCustomizedForPartner(partnerId)) {
			if (!partnerId.equals(partnerStatus.getPartnerId())) {
				throw new IllegalArgumentException("partner status does not belong to partner");
			}
		} else if (partnerStatus.getPartnerId() != null) {
			throw new IllegalArgumentException("partner status does not belong to partner");
		}
		this.partnerPortalAuthorizer.can(user, PartnerPortalAuthorizer.ACTION_READ_AND_WRITE, partnerId);
	}

	private String getPartnerIdOfEvaluation(final String evaluationId) {
		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(evaluationId).get();
		final JobRoleDAO jobRoleDAO = this.jobRoleManager.getJobRoleFromEvaluation(evaluationDAO).get();
		final PartnerCompanyDAO partnerCompanyDAO = this.partnerCompanyRepository
				.findByCompanyId(jobRoleDAO.getCompanyId()).get();
		return partnerCompanyDAO.getId();
	}

	private String getEvaluationIdFromResourceObject(final Object resource) {
		return ((Map<String, String>) resource).get("evaluationId");
	}

	private String getPartnerStatusIdFromResourceObject(final Object resource) {
		return ((Map<String, String>) resource).get("partnerStatusId");
	}
}
