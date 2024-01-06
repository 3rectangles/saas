/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.status.graphql;

import com.barraiser.onboarding.auth.AuthorizationResourceDTO;
import com.barraiser.onboarding.auth.Authorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.onboarding.interview.evaluation.auth.EvaluationPartnerStatusAuthorizer;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import com.barraiser.onboarding.interview.status.graphql.input.PartnerStatusInput;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
@Log4j2
public class EvaluationPartnerStatusMutation implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final Authorizer authorizer;
	private final EvaluationStatusManager evaluationStatusManager;

	@Override
	public String type() {
		return MUTATION_TYPE;
	}

	@Override
	public String name() {
		return "transitionPartnerStatus";
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final PartnerStatusInput input = this.graphQLUtil.getArgument(environment, "input", PartnerStatusInput.class);

		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);

		final AuthorizationResourceDTO authorizationResource = AuthorizationResourceDTO.builder()
				.type(EvaluationPartnerStatusAuthorizer.RESOURCE_TYPE)
				.resource(Map.of(
						"evaluationId", input.getEvaluationId(),
						"partnerStatusId", input.getStatusId()))
				.build();
		this.authorizer.can(authenticatedUser, EvaluationPartnerStatusAuthorizer.ACTION_WRITE, authorizationResource);

		this.evaluationStatusManager.transitionPartnerStatus(input.getEvaluationId(), input.getStatusId(),
				authenticatedUser.getUserName());

		return true;
	}
}
