/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.availability;

import com.barraiser.onboarding.auth.AuthorizationResourceDTO;
import com.barraiser.onboarding.auth.Authorizer;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.CandidateAvailabilityDAO;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.input.GetCandidateAvailabilityInput;
import com.barraiser.onboarding.interview.auth.InterviewAuthorizer;
import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class GetCandidateAvailability implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final CandidateAvailabilityManager candidateAvailabilityManager;
	private final Authorizer authorizer;

	@Override
	public String name() {
		return "getCandidateAvailability";
	}

	@Override
	public String type() {
		return QUERY_TYPE;
	}

	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(environment);
		final GetCandidateAvailabilityInput input = this.graphQLUtil.getInput(environment,
				GetCandidateAvailabilityInput.class);
		final AuthorizationResourceDTO authorizationResource = AuthorizationResourceDTO.builder()
				.type(InterviewAuthorizer.RESOURCE_TYPE)
				.resource(input.getInterviewId())
				.build();
		this.authorizer.can(user, InterviewAuthorizer.ACTION_READ_AND_WRITE_PREFERRED_SLOTS, authorizationResource);
		final List<CandidateAvailabilityDAO> candidateAvailabilityDAOs = this.candidateAvailabilityManager
				.getCandidateAvailabilitySlots(input.getInterviewId());
		return DataFetcherResult.newResult()
				.data(candidateAvailabilityDAOs).build();
	}
}
