/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.evaluation;

import com.barraiser.commons.eventing.Event;
import com.barraiser.commons.eventing.EventListener;
import com.barraiser.commons.eventing.SuppressFailure;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.candidateaddition.CandidateAddition;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.evaluationcancelled.EvaluationCancellation;
import com.barraiser.commons.eventing.schema.barraiser_interviewing.evaluationcompleted.EvaluationCompleted;
import com.barraiser.communication.CommunicationConsumer;
import com.barraiser.communication.message.SendMessageSlack;
import com.barraiser.communication.pojo.SlackMessageParameters;
import com.barraiser.onboarding.communication.ClientCommunicationService;
import com.barraiser.onboarding.communication.IntervieweeFeedbackCommunicationService;
import com.barraiser.onboarding.dal.*;
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
public class EvaluationCommunicationHandler implements EventListener<CommunicationConsumer> {

	private final CompanyRepository companyRepository;
	private final ClientCommunicationService clientCommunicationService;
	private final IntervieweeFeedbackCommunicationService intervieweeFeedbackCommunicationService;
	private SendMessageSlack sendMessageSlack;
	private final ObjectMapper objectMapper;
	private final EvaluationRepository evaluationRepository;

	public static final String EvaluationCompletionEventType = "EvaluationCompleted";
	public static final String EvaluationCancellationEventType = "EvaluationCancellation";

	public static final String CandidateAdditionEventType = "CandidateAddition";

	public final EvaluationManager evaluationManager;

	@Override
	public List<Class> eventsToListen() {
		return null;
	}

	@Override
	public void handleEvent(final Event event) throws Exception {

		if (EvaluationCompletionEventType.equals(event.getEventType())) {
			final EvaluationCompleted evaluationCompletedEvent = this.objectMapper.convertValue(event.getPayload(),
					EvaluationCompleted.class);
			final EvaluationDAO evaluation = this.evaluationRepository
					.findById(evaluationCompletedEvent.getEvaluation().getId()).get();

			final CompanyDAO company = this.companyRepository
					.findById(evaluation.getCompanyId())
					.orElseThrow(
							() -> new RuntimeException("No company exist for the evaluation : " + evaluation.getId()));

			if (Boolean.TRUE.equals(company.getSendClientMail())) {
				this.clientCommunicationService.sendEvaluationDoneMailToClient(evaluation);
			}

			this.createMessageForEvaluationCompletion(evaluationCompletedEvent, event);
			this.intervieweeFeedbackCommunicationService.sendIntervieweeFeedbacksForEvaluation(evaluation);
		} else if (EvaluationCancellationEventType.equals(event.getEventType())) {
			final EvaluationCancellation evaluationCancelledEvent = this.objectMapper.convertValue(event.getPayload(),
					EvaluationCancellation.class);

			this.createMessageForEvaluationCancellation(evaluationCancelledEvent, event);

		} else if (CandidateAdditionEventType.equals(event.getEventType())) {
			final CandidateAddition candidateAdditionEvent = this.objectMapper.convertValue(event.getPayload(),
					CandidateAddition.class);

			this.createMessageForCandidateAddition(candidateAdditionEvent);
		}
	}

	private void createMessageForEvaluationCompletion(final EvaluationCompleted evaluationCompletedEvent,
			final Event event) {
		final String evaluationId = evaluationCompletedEvent.getEvaluation().getId();

		final SlackMessageParameters slackMessage = SlackMessageParameters.builder()
				.partnerId(evaluationCompletedEvent.getEvaluation().getPartnerId())
				.jobRole(evaluationCompletedEvent.getEvaluation().getJobRole().getName())
				.evaluationId(evaluationId)
				.eventType(event.getEventType())
				.domain(this.evaluationManager.getDomainOfEvaluation(evaluationId).getName())
				.build();

		final String[] candidateName = evaluationCompletedEvent.getEvaluation().getCandidate().getName().split("\\s+");
		if (candidateName[1].equals("null")) {
			slackMessage.setUserName(candidateName[0]);
		} else {
			slackMessage.setUserName(evaluationCompletedEvent.getEvaluation().getCandidate().getName());
		}
		this.sendMessageSlack.sendMessageToRecipients(slackMessage);
	}

	private void createMessageForEvaluationCancellation(final EvaluationCancellation evaluationCancelledEvent,
			final Event event) {
		final String evaluationId = evaluationCancelledEvent.getEvaluation().getId();
		final SlackMessageParameters slackMessage = SlackMessageParameters.builder()
				.partnerId(evaluationCancelledEvent.getEvaluation().getPartnerId())
				.jobRole(evaluationCancelledEvent.getEvaluation().getJobRole().getName())
				.evaluationId(evaluationId)
				.eventType(event.getEventType())
				.domain(this.evaluationManager.getDomainOfEvaluation(evaluationId).getName())
				.build();

		final String[] candidateName = evaluationCancelledEvent.getEvaluation().getCandidate().getName().split("\\s+");
		if (candidateName[1].equals("null")) {
			slackMessage.setUserName(candidateName[0]);
		} else {
			slackMessage.setUserName(evaluationCancelledEvent.getEvaluation().getCandidate().getName());
		}
		this.sendMessageSlack.sendMessageToRecipients(slackMessage);
	}

	private void createMessageForCandidateAddition(final CandidateAddition candidateAdditionEvent) {
		final String evaluationId = candidateAdditionEvent.getCandidate().getEvaluationId();

		final SlackMessageParameters slackMessage = SlackMessageParameters.builder()
				.partnerId(candidateAdditionEvent.getCandidate().getPartnerId())
				.jobRole(candidateAdditionEvent.getCandidate().getJobRole().getName())
				.evaluationId(evaluationId)
				.eventType(CandidateAdditionEventType)
				.userName(candidateAdditionEvent.getCandidate().getUserName())
				.domain(this.evaluationManager.getDomainOfEvaluation(evaluationId).getName())
				.build();

		this.sendMessageSlack.sendMessageToRecipients(slackMessage);
	}
}
