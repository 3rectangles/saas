/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.input.SubmitFeedbackInput;
import com.barraiser.common.graphql.types.*;
import com.barraiser.common.monitoring.Profiled;
import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.feedbacksubmittedevent.FeedbackSubmittedEvent;
import com.barraiser.commons.eventing.schema.commons.InterviewEvent;
import com.barraiser.onboarding.auth.AuthenticationException;
import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.communication.FeedbackInconsistencyCommunicationService;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.events.InterviewingEventProducer;
import com.barraiser.onboarding.graphql.DataFetcherType;
import com.barraiser.onboarding.graphql.GraphQLUtil;
import com.barraiser.onboarding.graphql.NamedDataFetcher;
import com.barraiser.onboarding.interview.evaluation.BgsDataGenerator;

import com.fasterxml.jackson.core.JsonProcessingException;
import graphql.GraphQLContext;
import graphql.schema.DataFetchingEnvironment;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import static com.barraiser.onboarding.common.Constants.ROUND_TYPE_INTERNAL;

@Log4j2
@RequiredArgsConstructor
@Component
public class SubmitFeedbackMutation implements NamedDataFetcher {
	private final GraphQLUtil graphQLUtil;
	private final FeedbackDBUpdator feedbackDBUpdator;
	private final InterviewService interviewService;
	private final FeedbackInconsistencyCommunicationService feedbackInconsistencyCommunicationService;
	private final InterviewNotificationService interviewNotificationService;
	private final InterviewingEventProducer eventProducer;
	private final InterviewCompletionService interviewCompletionService;
	private final InterviewStatusManager interviewStatusManager;
	private final BgsDataGenerator bgsDataGenerator;

	@Override
	public String name() {
		return "submitFeedback";
	}

	@Override
	public String type() {
		return DataFetcherType.MUTATION.getValue();
	}

	@Profiled(name = "submitFeedback")
	@Override
	public Object get(final DataFetchingEnvironment environment) throws Exception {
		final GraphQLContext context = environment.getContext();
		final AuthenticatedUser authenticatedUser = context
				.get(com.barraiser.onboarding.graphql.Constants.CONTEXT_KEY_USER);

		if (authenticatedUser == null) {
			throw new AuthenticationException("No authenticated user found");
		}
		final SubmitFeedbackInput input = this.graphQLUtil.getInput(environment, SubmitFeedbackInput.class);
		InterviewDAO interviewDAO = this.interviewService.findById(input.getInterviewId());
		final SubmitFeedbackResult result = this.feedbackDBUpdator.save(interviewDAO, input, authenticatedUser);

		if (Boolean.TRUE.equals(input.getFinalSubmission())
				&& (result.getFeedbackImprovements() == null || result.getFeedbackImprovements().isEmpty())) {
			this.interviewNotificationService.sendFeedbackCompletedEmail(input.getInterviewId());
			this.generateFeedbackSubmittedEvent(input.getInterviewId());
			interviewDAO = this.interviewService.findById(input.getInterviewId());
			interviewDAO = this.interviewService.save(
					interviewDAO.toBuilder().feedbackStatus("SUBMITTED").build());
			this.updateInterviewStatus(interviewDAO, input.getIsFeedbackInconsistent(),
					authenticatedUser.getUserName());
			if (Boolean.TRUE.equals(input.getIsFeedbackInconsistent())) {
				this.feedbackInconsistencyCommunicationService.sendEmailToExpert(
						input.getInterviewId());
			}
		}
		return result;
	}

	public void updateInterviewStatus(
			final InterviewDAO interviewDAO, final Boolean isFeedbackInconsistent, final String userId)
			throws JsonProcessingException {

		final String interviewStatusInDb = this.interviewService.findById(interviewDAO.getId()).getStatus();

		if ((!interviewDAO.getIsTaggingAgentNeeded()
				&&
				InterviewStatus.PENDING_INTERVIEWING.getValue().equals(interviewStatusInDb))
				||
				(InterviewStatus.PENDING_FEEDBACK_SUBMISSION.getValue().equals(interviewStatusInDb))) {
			if (ROUND_TYPE_INTERNAL.equalsIgnoreCase(interviewDAO.getInterviewRound())) {
				this.bgsDataGenerator.generateBgsDataForInterview(interviewDAO);
				this.interviewCompletionService.finishInterview(interviewDAO);
			} else {
				this.bgsDataGenerator.generateBgsDataForInterview(interviewDAO);
				this.interviewStatusManager.updateInterviewStatus(interviewDAO, InterviewStatus.PENDING_QC, userId,
						null);
			}
		}

		if (Boolean.TRUE.equals(isFeedbackInconsistent)
				&& InterviewStatus.PENDING_QC.getValue().equals(interviewStatusInDb)) {
			this.interviewStatusManager.updateInterviewStatus(interviewDAO, InterviewStatus.PENDING_CORRECTION, userId,
					null);
		} else if (Boolean.FALSE.equals(isFeedbackInconsistent)
				&& InterviewStatus.PENDING_QC.getValue().equals(interviewStatusInDb)) {
			this.interviewCompletionService.finishInterview(interviewDAO);
		}

		if (InterviewStatus.PENDING_CORRECTION.getValue().equals(interviewStatusInDb)) {
			this.interviewStatusManager.updateInterviewStatus(interviewDAO, InterviewStatus.PENDING_QC, userId, null);
		}
	}

	@SneakyThrows
	private void generateFeedbackSubmittedEvent(final String interviewId) {
		final Event<FeedbackSubmittedEvent> feedbackSubmittedEvent = new Event<>();
		feedbackSubmittedEvent.setPayload(
				new FeedbackSubmittedEvent().interview(new InterviewEvent().id(interviewId)));
		this.eventProducer.pushEvent(feedbackSubmittedEvent);
	}

}
