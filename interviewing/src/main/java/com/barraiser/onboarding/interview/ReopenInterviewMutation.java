/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.auth.AuthorizationResourceDTO;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.graphql.GraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.auth.InterviewAuthorizer;
import com.barraiser.onboarding.interview.graphql.input.ReopenInterviewInput;
import graphql.schema.DataFetchingEnvironment;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class ReopenInterviewMutation implements GraphQLMutation<Boolean> {
	public static final String ERROR_MESSAGE = "Interview not in terminal State, cannot be re-opened";

	private final GraphQLUtil graphQLUtil;
	private final InterViewRepository interViewRepository;
	private final ReopenInterviewManager reopenInterviewManager;
	private final InterviewAuthorizer authorizer;

	@Override
	public String name() {
		return "reopenInterview";
	}

	@Override
	public Boolean get(final DataFetchingEnvironment environment) throws Exception {
		final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(environment);
		final ReopenInterviewInput input = this.graphQLUtil.getInput(environment, ReopenInterviewInput.class);
		final AuthorizationResourceDTO authorizationResource = AuthorizationResourceDTO.builder()
				.type(InterviewAuthorizer.RESOURCE_TYPE)
				.resource(input.getInterviewId())
				.build();
		this.authorizer.can(user, InterviewAuthorizer.ACTION_REOPEN, authorizationResource);
		this.reopenInterview(input, user);
		return true;
	}

	private void validateData(final InterviewDAO interviewDAO) {
		if (!(InterviewStatus.CANCELLATION_DONE.getValue().equals(interviewDAO.getStatus()) ||
				InterviewStatus.DONE.getValue().equals(interviewDAO.getStatus()))) {
			throw new IllegalArgumentException(ERROR_MESSAGE);
		}
	}

	private void reopenInterview(final ReopenInterviewInput input, final AuthenticatedUser user) {
		final InterviewDAO interviewDAO = this.interViewRepository.findById(input.getInterviewId()).get();
		this.validateData(interviewDAO);
		this.reopenInterviewManager.reopenInterview(interviewDAO, input.getReasonId(), user.getUserName());
	}
}
