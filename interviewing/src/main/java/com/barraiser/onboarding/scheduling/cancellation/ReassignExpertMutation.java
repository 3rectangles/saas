/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.graphql.GraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.scheduling.cancellation.graphql.input.ReassignExpertInput;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.ExpertReassignmentManager;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class ReassignExpertMutation implements GraphQLMutation<Boolean> {
	private final GraphQLUtil graphQLUtil;
	private final ExpertReassignmentManager expertReassignmentManager;

	@Override
	public String name() {
		return "reassignExpert";
	}

	@Override
	public Boolean get(final DataFetchingEnvironment environment) throws Exception {
		final ReassignExpertInput input = this.graphQLUtil.getInput(environment, ReassignExpertInput.class);
		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);
		this.expertReassignmentManager.reassignExpert(input.getInterviewId(), input.getReassignmentReason(),
				authenticatedUser);
		return true;
	}
}
