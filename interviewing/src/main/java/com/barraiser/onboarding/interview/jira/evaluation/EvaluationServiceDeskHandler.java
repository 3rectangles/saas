/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.evaluation;

import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.CommentUtil;
import com.barraiser.onboarding.interview.jira.JiraEventHandler;
import com.barraiser.onboarding.interview.jira.JiraFieldManager;
import com.barraiser.onboarding.interview.jira.JiraUtil;
import com.barraiser.onboarding.interview.jira.client.JiraClient;
import com.barraiser.onboarding.interview.jira.dto.*;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Log4j2
@Component
@AllArgsConstructor
public class EvaluationServiceDeskHandler implements JiraEventHandler {
	public static final String JIRA_ISSUE_TYPE_ID_Evaluation = "10102";
	public static final String PROBLEM_WITH_JIRA_SERVER = "Problem with jira server";
	private static final String TRANSITIONED_BY_BARRAISER = "BarRaiser";

	private final EvaluationFieldUpdator evaluationFieldUpdator;
	private final EvaluationStatusManager evaluationStatusManager;
	private final JiraUUIDRepository jiraUUIDRepository;
	private final EvaluationRepository evaluationRepository;
	private final JiraClient jiraClient;
	private final FollowUpDateUpdater followUpDateUpdater;

	private final WaitingInformationUpdater waitingInformationUpdater;
	private final InterviewUpdater interviewUpdater;
	private final JiraUtil jiraUtil;
	private final CommentUtil commentUtil;
	private final ReasonRepository reasonRepository;
	private final JiraFieldManager jiraFieldManager;

	@Override
	public String projectId() {
		return JIRA_ISSUE_TYPE_ID_Evaluation;
	}

	@Override
	public void handleEvent(final JiraEvent event) {
		Boolean issueNotFound = false;
		ResponseEntity<EvaluationServiceDeskIssue> result = null;
		if (JiraConstants.JIRA_COMMENTS_STATUS.contains(event.getBody().getWebhookEvent())) {
			this.commentUtil.actionCommentToDB(event, event.getBody().getWebhookEvent());
			return;
		}
		try {
			result = this.jiraClient.getEvaluationServiceDeskIssue(event.getIssue());
			if (result.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
				issueNotFound = true;
			}
			if (result.getStatusCode().isError()) {
				log.error(PROBLEM_WITH_JIRA_SERVER);
				throw new HttpClientErrorException(result.getStatusCode(),
						"Error Received from Jira Server for issue Id: " + event.getIssue());
			}
		} catch (final FeignException.NotFound e) {
			issueNotFound = true;
		} catch (final Exception e) {
			log.error(PROBLEM_WITH_JIRA_SERVER, e);
			throw new RuntimeException(PROBLEM_WITH_JIRA_SERVER, e);
		}
		if (event.getBody().getWebhookEvent().equals("jira:issue_deleted") && issueNotFound) {
			this.softDeleteEvaluation(event);
			return;
		}
		this.processChange(Objects.requireNonNull(result.getBody()));
	}

	private void processChange(final EvaluationServiceDeskIssue evaluationIssue) {

		final EvaluationServiceDeskIssue.Fields fields = evaluationIssue.getFields();
		final EvaluationDAO originalEvaluation = this.getEvaluation(evaluationIssue);

		final EvaluationDAO updatedEvaluation = this.evaluationFieldUpdator.update(fields, originalEvaluation,
				originalEvaluation.getCandidateId());

		if (EvaluationStatus.WAITING_CLIENT.getValue().equals(fields.getStatus().getName())
				&& fields.getWaitingClientReason() != null) {

			this.waitingInformationUpdater.update(this.getWaitingClientReasonDAO(fields.getWaitingClientReason()),
					updatedEvaluation.getId());
			if (WaitingClientReason.CANDIDATE_IS_PENDING_FOR_SCHEDULING.getValue()
					.equalsIgnoreCase(this.jiraUtil.getValueFromString(fields.getWaitingClientReason().getValue()))) {
				this.interviewUpdater.updatePendingScheduling(updatedEvaluation);
			}
		}

		if (!EvaluationStatus.CANCELLED.equals(EvaluationStatus.fromString(fields.getStatus().getName()))) {
			final EvaluationDAO updatedEvaluationDAO = this.evaluationStatusManager.transitionBarRaiserStatus(
					updatedEvaluation.getId(), fields.getStatus().getName(),
					TRANSITIONED_BY_BARRAISER, this.getChangeLogs(evaluationIssue, EvaluationStatus.DONE));

			this.followUpDateUpdater.update(originalEvaluation, updatedEvaluationDAO, evaluationIssue.getKey());
		}

	}

	private List<JiraChangeLogsResponse.ChangeLog> getChangeLogs(final EvaluationServiceDeskIssue evaluationIssue,
			final EvaluationStatus evaluationStatus) {
		final List<JiraChangeLogsResponse.ChangeLog> changeLogs = this.jiraFieldManager.getChangeLogsForField(
				evaluationIssue.getKey(),
				evaluationStatus.getValue());
		return changeLogs;
	}

	private ReasonDAO getWaitingClientReasonDAO(final IdValueField waitingClientReason) {
		if (waitingClientReason == null)
			return null;
		return this.reasonRepository.findById(this.jiraUtil.getIdFromString(waitingClientReason.getValue()))
				.orElseThrow(
						() -> new RuntimeException(
								"Waiting Reason not mapped: {}"
										+ this.jiraUtil.getValueFromString(waitingClientReason.getValue())));
	}

	private EvaluationDAO getEvaluation(final EvaluationServiceDeskIssue evaluationIssue) {
		final String evaluationId = this.getIdForEvaluation(evaluationIssue);
		// We expect the evaluation to be present in database by the time JIRA sync is
		// triggered.
		return this.evaluationRepository
				.findById(evaluationId)
				.orElseThrow(() -> new RuntimeException(
						"No evaluation found for the evaluationId: " + evaluationIssue.getKey()));
	}

	public void softDeleteEvaluation(final JiraEvent event) {
		final JiraUUIDDAO jiraUUIDDAO = this.jiraUUIDRepository.findByJira(event.getIssue()).orElse(null);
		this.evaluationRepository.save(this.evaluationRepository.findById(jiraUUIDDAO.getUuid()).get().toBuilder()
				.deletedOn(Instant.now().getEpochSecond()).build());
	}

	private String getIdForEvaluation(final EvaluationServiceDeskIssue evaluationIssue) {
		final String evaluationId = evaluationIssue.getFields().getEntityId();

		if (Strings.isBlank(evaluationId)) {
			final JiraUUIDDAO jiraUUID = this.jiraUUIDRepository.findByJira(evaluationIssue.getKey())
					.orElseThrow(
							() -> new RuntimeException("No evaluation found for jira: " + evaluationIssue.getKey()));
			return jiraUUID.getUuid();
		}
		return evaluationId;
	}

}
