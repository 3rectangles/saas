/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.auth;

import com.barraiser.common.graphql.types.Evaluation;
import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.auth.ResourceAuthorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.partner.EvaluationManager;
import com.barraiser.onboarding.partner.auth.PartnerPortalAuthorizer;
import com.barraiser.onboarding.user.PartnerEmployeeWhiteLister;
import com.barraiser.commons.auth.UserRole;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Log4j2
public class EvaluationAuthorizer implements ResourceAuthorizer<Evaluation> {
	public static final String RESOURCE_TYPE = "evaluation";
	public static final String ACTION_READ = "read_evaluation";

	private final PartnerPortalAuthorizer partnerPortalAuthorizer;
	private final PartnerEmployeeWhiteLister partnerEmployeeWhiteLister;
	private final EvaluationManager evaluationManager;

	@Override
	public String type() {
		return RESOURCE_TYPE;
	}

	@Override
	public void can(final AuthenticatedUser user, final String action, final Evaluation evaluation)
			throws AuthorizationException {
		if (ACTION_READ.equals(action)) {
			this.canRead(user, evaluation);
		} else {
			throw new IllegalArgumentException(
					"no valid action found " + action + " on resource type " + RESOURCE_TYPE);
		}
	}

	public boolean isSuperUser(final AuthenticatedUser user) {
		return user.getRoles().contains(UserRole.OPS) || user.getRoles().contains(UserRole.ADMIN)
				|| user.getRoles().contains(UserRole.QC);
	}

	private void canRead(final AuthenticatedUser user, final Evaluation evaluation)
			throws AuthorizationException {

		if (this.isSuperUser(user)) {
			return;
		}
		final String partnerCompanyId = this.evaluationManager.getPartnerCompanyForEvaluation(evaluation.getId());

		if (Boolean.TRUE.equals(this.evaluationManager.isDemoCompany(partnerCompanyId))) {
			return;
		}

		// TODO: We will get rid of this once we have a proper authorization in place
		if (this.partnerEmployeeWhiteLister.isUserDomainBlackListedForPartner(user.getEmail(), partnerCompanyId)) {
			throw new AuthorizationException();
		}

		if (!this.partnerPortalAuthorizer.isPartnerRep(user, partnerCompanyId)
				&& !this.partnerEmployeeWhiteLister.isUserWhiteListedForPartner(
						user.getEmail(), partnerCompanyId)) {
			throw new AuthorizationException();
		}
	}
}
