/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.graphql.resolver;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.graphql.Constants;
import com.barraiser.onboarding.graphql.GraphQLMutation;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.interview.CancellationReasonManager;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.scheduling.cancellation.InterviewCancellationManager;
import com.barraiser.onboarding.scheduling.cancellation.graphql.input.CancelInterviewInput;

import graphql.schema.DataFetchingEnvironment;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Log4j2
@Component
@AllArgsConstructor
public class CancelInterviewMutation implements GraphQLMutation {
	private final GraphQLUtil graphQLUtil;
	private final InterviewCancellationManager interviewCancellationManager;
	private final InterViewRepository interViewRepository;
	private final CancellationReasonManager cancellationReasonManager;

	static final String GENERIC_ERROR_RESPONSE = "There was some technical error. Please contact support.";

	@Override
	public Boolean get(final DataFetchingEnvironment environment) throws Exception {
		final CancelInterviewInput input = this.graphQLUtil.getArgument(
				environment, Constants.CONTEXT_KEY_INPUT, CancelInterviewInput.class);

		final AuthenticatedUser authenticatedUser = this.graphQLUtil.getLoggedInUser(environment);

		final InterviewDAO interviewToBeCancelled = this.interViewRepository.findById(input.getInterviewId()).get();

		return this.cancelInterview(interviewToBeCancelled, input, authenticatedUser.getUserName());
	}

	private boolean cancelInterview(
			final InterviewDAO interviewToBeCancelled,
			final CancelInterviewInput input,
			final String cancelledBy) {

		final String cancellationReasonId = input.getCancellationReasonId() != null
				? input.getCancellationReasonId()
				: input.getCancellationReason().getId();
		final InterviewDAO interview = interviewToBeCancelled.toBuilder()
				.cancellationTime(String.valueOf(Instant.now().getEpochSecond()))
				.cancellationReasonId(cancellationReasonId)
				.isRescheduled(
						this.cancellationReasonManager.isCancelledByExpert(
								cancellationReasonId))
				.build();

		try {
			// Leaving soure null here.
			return this.interviewCancellationManager.cancel(interview, cancelledBy, null);
		} catch (final Exception e) {
			log.error(e, e);
			throw new RuntimeException(GENERIC_ERROR_RESPONSE, e);
		}
	}

	@Override
	public String name() {
		return "cancelInterview";
	}
}
