/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.reject_candidate;

import com.barraiser.common.graphql.RejectCandidateInput;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.dal.StatusDAO;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.GraphQLAbacAuthorizer;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.PartnerConfigManager;
import com.barraiser.onboarding.interview.evaluation.EvaluationPartnerStatus;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import com.barraiser.onboarding.partner.auth.PartnerPortalAuthorizer;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class RejectCandidateAuthorizer implements GraphQLAbacAuthorizer {
	public static final String USER_UNAUTHORISED_TO_ACCESS_REJECTION_STATUS = "You are not authorized to reject candidates.";

	private final PartnerConfigManager partnerConfigManager;
	private final GraphQLUtil graphQLUtil;
	private final EvaluationStatusManager evaluationStatusManager;
	private final PartnerPortalAuthorizer partnerPortalAuthorizer;

	@Override
	public AuthorizationResult authorize(final DataFetchingEnvironment environment) {
		final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(environment);
		final RejectCandidateInput input = this.graphQLUtil.getInput(environment, RejectCandidateInput.class);
		final String partnerId = this.partnerConfigManager.getPartnerCompanyForEvaluation(input.getEvaluationId());
		this.isUserAuthorisedToAccessRejectionStatus(partnerId);
		this.partnerPortalAuthorizer.can(user, PartnerPortalAuthorizer.ACTION_READ_AND_WRITE, partnerId);

		return AuthorizationResult.builder()
				.build();
	}

	private void isUserAuthorisedToAccessRejectionStatus(final String partnerId) {
		final Optional<StatusDAO> partnerStatus = this.evaluationStatusManager.getPartnerStatusByInternalStatus(
				partnerId,
				EvaluationPartnerStatus.REJECTED);
		if (partnerStatus.isEmpty()) {
			throw new AuthorizationException(USER_UNAUTHORISED_TO_ACCESS_REJECTION_STATUS);
		}
	}
}
