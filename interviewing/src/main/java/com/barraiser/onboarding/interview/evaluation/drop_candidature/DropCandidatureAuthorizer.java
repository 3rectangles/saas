/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.drop_candidature;

import com.barraiser.common.graphql.input.availability.UpdateRecurringAvailabilityInput;
import com.barraiser.common.graphql.types.Evaluation;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.auth.AuthorizationException;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.GraphQLAbacAuthorizer;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.PartnerConfigManager;
import com.barraiser.onboarding.partner.PartnerRepAuthorizer;
import com.barraiser.onboarding.user.auth.SuperAdminAuthorizer;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class DropCandidatureAuthorizer implements GraphQLAbacAuthorizer {

	public static final String ERROR_MESSAGE_FOR_UNAUTHENTICATED_USER = "User does not have permission to Drop Candidature.";

	private final PartnerConfigManager partnerConfigManager;
	private final EvaluationRepository evaluationRepository;
	private final GraphQLUtil graphQLUtil;
	private final SuperAdminAuthorizer superAdminAuthorizer;
	private final PartnerRepAuthorizer partnerRepAuthorizer;

	@Override
	public AuthorizationResult authorize(DataFetchingEnvironment environment) {
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		final String evaluationId = environment.getArgument("evaluationId");
		final String partnerId = this.getPartnerId(evaluationId);

		if (!this.superAdminAuthorizer.isSuperAdmin(authenticatedUser)
				&& !this.partnerRepAuthorizer.isPartnerRepForPartner(authenticatedUser, partnerId)) {
			throw new AuthorizationException(ERROR_MESSAGE_FOR_UNAUTHENTICATED_USER);
		}

		return AuthorizationResult.builder()
				.build();
	}

	private String getPartnerId(final String evaluationId) {
		final String companyId = this.evaluationRepository.findById(evaluationId).get().getCompanyId();
		return this.partnerConfigManager.getPartnerIdFromCompanyId(companyId);
	}
}
