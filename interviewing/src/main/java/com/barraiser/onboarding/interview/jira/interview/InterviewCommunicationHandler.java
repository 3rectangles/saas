/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.interview;

import com.barraiser.communication.CommunicationConsumer;
import com.barraiser.communication.message.SendMessageSlack;
import com.barraiser.communication.pojo.SlackMessageParameters;
import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.SuppressFailure;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.interviewcancellation.InterviewCancellation;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.interviewcompletion.InterviewCompletion;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.partner.EvaluationManager;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@SuppressFailure
@Component
@AllArgsConstructor
public class InterviewCommunicationHandler implements EventListener<CommunicationConsumer> {

	private final ObjectMapper objectMapper;
	private final InterViewRepository interViewRepository;
	private SendMessageSlack sendMessageSlack;
	private final CancellationReasonRepository cancellationReasonRepository;
	public static final String InterviewCompletionEventType = "InterviewCompletion";
	public static final String InterviewCancellationEventType = "InterviewCancellation";

	public final EvaluationManager evaluationManager;

	@Override
	public List<Class> eventsToListen() {
		return null;
	}

	@Override
	public void handleEvent(final Event event) throws Exception {

		if (InterviewCompletionEventType.equals(event.getEventType())) {

			final InterviewCompletion interviewCompletionEvent = this.objectMapper.convertValue(event.getPayload(),
					InterviewCompletion.class);
			final String lastName = interviewCompletionEvent.getInterview().getInterviewee().getLastName();
			this.createMessageBodyForInterviewCompletion(interviewCompletionEvent, lastName, event);
		}
	}

	private void createMessageBodyForInterviewCompletion(
			final InterviewCompletion interviewCompletionEvent,
			final String lastName,
			final Event event) {
		final String evaluationId = interviewCompletionEvent.getInterview().getEvaluationId();

		final SlackMessageParameters slackMessage = SlackMessageParameters.builder()
				.partnerId(interviewCompletionEvent.getInterview().getPartnerId())
				.evaluationId(evaluationId)
				.jobRole(interviewCompletionEvent.getInterview().getJobRole().getName())
				.eventType(event.getEventType())
				.interviewRound(interviewCompletionEvent.getInterview().getInterviewRound())
				.domain(
						this.evaluationManager
								.getDomainOfEvaluation(evaluationId)
								.getName())
				.build();

		if (lastName != null) {
			String userName = String.format(
					"%s %s",
					interviewCompletionEvent.getInterview().getInterviewee().getFirstName(),
					lastName);
			slackMessage.setUserName(userName);

		} else {
			slackMessage.setUserName(
					interviewCompletionEvent.getInterview().getInterviewee().getFirstName());
		}
		this.sendMessageSlack.sendMessageToRecipients(slackMessage);
	}

	private void createMessageForInterviewCancellation(
			final InterviewCancellation interviewCancellationEvent,
			final String lastName,
			final Event event) {

		final InterviewDAO interviewFromEvent = this.interViewRepository
				.findById(interviewCancellationEvent.getInterview().getId())
				.get();
		final CancellationReasonDAO cancellationReasonInfo = this.cancellationReasonRepository
				.findById(interviewFromEvent.getCancellationReasonId())
				.get();
		final String cancellationReasonFromId = cancellationReasonInfo.getCancellationReason();
		final String cancellationType = cancellationReasonInfo.getCancellationType().toUpperCase();
		final String evaluationId = interviewCancellationEvent.getInterview().getEvaluationId();

		final SlackMessageParameters slackMessage = SlackMessageParameters.builder()
				.partnerId(interviewCancellationEvent.getInterview().getPartnerId())
				.evaluationId(evaluationId)
				.jobRole(interviewCancellationEvent.getInterview().getJobRole().getName())
				.eventType(event.getEventType())
				.interviewRound(
						interviewCancellationEvent.getInterview().getInterviewRound())
				.cancellationReason(cancellationReasonFromId)
				.cancellationType(cancellationType)
				.domain(
						this.evaluationManager
								.getDomainOfEvaluation(evaluationId)
								.getName())
				.build();

		if (lastName != null) {

			String userName = String.format(
					"%s %s",
					interviewCancellationEvent
							.getInterview()
							.getInterviewee()
							.getFirstName(),
					lastName);
			slackMessage.setUserName(userName);
		} else {
			slackMessage.setUserName(
					interviewCancellationEvent.getInterview().getInterviewee().getFirstName());
		}
		this.sendMessageSlack.sendMessageToRecipients(slackMessage);
	}
}
