/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.graphql.AuthorizationResult;
import com.barraiser.onboarding.graphql.AuthorizedGraphQLMutation_deprecated;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.graphql.input.RedoInterviewInput;
import graphql.schema.DataFetchingEnvironment;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class RedoInterviewMutation extends AuthorizedGraphQLMutation_deprecated<Boolean> {
	public static final String ERROR_MESSAGE_WHEN_INTERVIEW_NOT_IN_TERMINAL_STATE = "An Error occurred, please refresh the page and try again.";
	public static final String ERROR_MESSAGE_WHEN_INTERVIEW_IS_ALREADY_REDONE = "Redo option has already been selected for the interview. Please refresh the page and try again.";

	private final GraphQLUtil graphQLUtil;
	private final InterViewRepository interViewRepository;
	private final InterviewRedoer interviewRedoer;
	private final InterviewUtil interviewUtil;

	@Override
	public String name() {
		return "redoInterview";
	}

	public RedoInterviewMutation(RedoInterviewAuthorizer abacAuthorizer, GraphQLUtil graphQLUtil,
			InterViewRepository interViewRepository, InterviewRedoer interviewRedoer,
			InterviewUtil interviewUtil) {
		super(abacAuthorizer);
		this.graphQLUtil = graphQLUtil;
		this.interViewRepository = interViewRepository;
		this.interviewRedoer = interviewRedoer;
		this.interviewUtil = interviewUtil;
	}

	@Override
	protected Boolean fetch(DataFetchingEnvironment environment, AuthorizationResult authorizationResult) {
		final AuthenticatedUser user = this.graphQLUtil.getLoggedInUser(environment);
		final RedoInterviewInput input = this.graphQLUtil.getInput(environment, RedoInterviewInput.class);
		this.redoInterview(input, user);
		return true;
	}

	private void redoInterview(final RedoInterviewInput input, final AuthenticatedUser user) {
		final InterviewDAO interviewDAO = this.interViewRepository.findById(input.getInterviewId()).get();
		this.validateData(interviewDAO);
		this.interviewRedoer.redoInterview(interviewDAO, input.getReasonId(), user.getUserName());
	}

	private void validateData(final InterviewDAO interviewDAO) {
		if (!InterviewStatus.DONE.getValue().equals(interviewDAO.getStatus())) {
			throw new IllegalArgumentException(ERROR_MESSAGE_WHEN_INTERVIEW_NOT_IN_TERMINAL_STATE);
		}

		if (Boolean.FALSE.equals(this.interviewUtil.shouldInterviewBeConsideredForEvaluation(interviewDAO))) {
			throw new IllegalArgumentException(ERROR_MESSAGE_WHEN_INTERVIEW_IS_ALREADY_REDONE);
		}
	}
}
