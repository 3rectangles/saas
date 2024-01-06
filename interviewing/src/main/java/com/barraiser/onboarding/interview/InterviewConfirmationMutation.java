/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.InterviewConfirmationDAO;
import com.barraiser.onboarding.dal.InterviewConfirmationRepository;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.graphql.Constants;
import com.barraiser.onboarding.graphql.DataFetcherType;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.common.graphql.input.ConfirmInterviewInput;
import com.barraiser.onboarding.scheduling.cancellation.InterviewCancellationManager;
import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Log4j2
@RequiredArgsConstructor
@Component
public class InterviewConfirmationMutation implements NamedDataFetcher {
	public static final String CANDIDATE = "CANDIDATE";
	public static final String ACTION_PERFORMED_BY_SYSTEM = "BarRaiser";

	private final GraphQLUtil graphQLUtil;
	private final InterViewRepository interViewRepository;
	private final InterviewConfirmationRepository interviewConfirmationRepository;
	private final InterviewCancellationManager interviewCancellationManager;

	@Override
	public String name() {
		return "confirmInterview";
	}

	@Override
	public String type() {
		return DataFetcherType.MUTATION.getValue();
	}

	@Transactional
	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {

		final AuthenticatedUser authenticatedUser = this.getAuthenticatedUser(environment);
		final ConfirmInterviewInput input = this.graphQLUtil.getInput(environment, ConfirmInterviewInput.class);
		final InterviewDAO interviewDAO = this.interViewRepository.findById(input.getInterviewId()).get();
		this.validateDataForConfirmation(interviewDAO, input.getCandidateConfirmation(),
				input.getInterviewerConfirmation());
		this.updateInterviewConfirmation(interviewDAO, input, authenticatedUser);
		this.cancelInterviewIfNeeded(interviewDAO, input);
		return input.getType().equals(CANDIDATE) ? input.getCandidateConfirmation()
				: input.getInterviewerConfirmation();
	}

	private InterviewConfirmationDAO createInterviewConfirmationDAO(final String interviewId, final String channel) {
		return InterviewConfirmationDAO.builder()
				.id(UUID.randomUUID().toString())
				.interviewId(interviewId)
				.communicationChannel(channel)
				.build();
	}

	private void validateDataForConfirmation(final InterviewDAO interviewDAO, final Boolean candidateConfirmation,
			final Boolean interviewerConfirmation) {
		if (candidateConfirmation == null && interviewerConfirmation == null) {
			throw new IllegalArgumentException("No confirmation present in input");
		}
		if (!List.of(InterviewStatus.PENDING_TA_ASSIGNMENT.getValue(), InterviewStatus.PENDING_INTERVIEWING.getValue())
				.contains(interviewDAO.getStatus())) {
			throw new IllegalArgumentException("Interview not in a state to take confirmation");
		}
	}

	private void updateInterviewConfirmation(final InterviewDAO interviewDAO, final ConfirmInterviewInput input,
			final AuthenticatedUser confirmationGivenBy) {
		InterviewConfirmationDAO interviewConfirmationDAO = this.interviewConfirmationRepository
				.findByInterviewIdAndCommunicationChannelAndRescheduleCount(input.getInterviewId(), input.getChannel(),
						interviewDAO.getRescheduleCount())
				.orElse(this.createInterviewConfirmationDAO(input.getInterviewId(), input.getChannel()));

		if (input.getType().equals(CANDIDATE)) {
			interviewConfirmationDAO = interviewConfirmationDAO.toBuilder()
					.candidateConfirmation(input.getCandidateConfirmation())
					.candidateConfirmationTime(Instant.now())
					.candidateConfirmationGivenBy(
							confirmationGivenBy != null ? confirmationGivenBy.getUserName() : null)
					.build();
		} else {
			interviewConfirmationDAO = interviewConfirmationDAO.toBuilder()
					.interviewerConfirmation(input.getInterviewerConfirmation())
					.interviewerConfirmationTime(Instant.now())
					.build();
		}
		interviewConfirmationDAO = interviewConfirmationDAO.toBuilder()
				.rescheduleCount(interviewDAO.getRescheduleCount()).build();
		this.interviewConfirmationRepository.save(interviewConfirmationDAO);
	}

	private void cancelInterviewIfNeeded(final InterviewDAO interviewDAO, final ConfirmInterviewInput input)
			throws Exception {
		if (input.getType().equals(CANDIDATE) && !input.getCandidateConfirmation()) {
			final InterviewDAO interviewToBeCancelled = interviewDAO
					.toBuilder()
					.cancellationReasonId(input.getCancellationReason().getId())
					.cancellationTime(String.valueOf(Instant.now().getEpochSecond()))
					.build();

			this.interviewCancellationManager.processInterviewCancelledInConfirmationFlow(
					interviewToBeCancelled,
					ACTION_PERFORMED_BY_SYSTEM,
					input.getSource());
		}
	}

	private AuthenticatedUser getAuthenticatedUser(final DataFetchingEnvironment environment) {
		final GraphQLContext context = environment.getContext();
		return context.get(Constants.CONTEXT_KEY_USER);
	}
}
