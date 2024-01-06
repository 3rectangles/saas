/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira;

import com.barraiser.onboarding.dal.JiraUUIDDAO;
import com.barraiser.onboarding.dal.JiraUUIDRepository;
import com.barraiser.onboarding.dal.JiraWorkflowRepository;
import com.barraiser.onboarding.interview.jira.client.JiraClient;
import com.barraiser.onboarding.interview.jira.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class JiraWorkflowManager {
	public static final String JIRA_ACTION_CANCEL_INTERVIEW_BOOKING = "CANCEL_INTERVIEW_BOOKING";
	public static final String JIRA_ACTION_SCHEDULE_INTERVIEW = "SCHEDULE_INTERVIEW";
	public static final String JIRA_PROJECT_ID_EVALUATION = "10028";

	private final JiraWorkflowRepository jiraWorkflowRepository;
	private final JiraUUIDRepository jiraUUIDRepository;
	private final JiraClient jiraClient;
	private final ObjectMapper objectMapper;

	public boolean shouldPerformAction(
			final String fromState, final String toState, final String action) {
		return this.jiraWorkflowRepository
				.findByFromStateAndToStateAndAction(fromState, toState, action)
				.isPresent();
	}

	public void transitionJiraStatus(final String uuid, final String toStatus) {
		final String jira = this.jiraUUIDRepository
				.findByUuid(uuid)
				.orElse(JiraUUIDDAO.builder().build())
				.getJira();
		try {
			final Transition transition = this.jiraClient.getTransitions(jira).getTransitions().stream()
					.filter(t -> t.getToState().getName().equals(toStatus))
					.findFirst()
					.orElseThrow(
							() -> new JiraTransitionNotFoundException(
									String.format(
											"Given status %s is not present in the"
													+ " workflow for jira %s",
											toStatus, jira)));

			final ObjectNode requestBody = this.objectMapper.createObjectNode();
			requestBody.putPOJO("transition", Transition.builder().id(transition.getId()).build());
			this.jiraClient.updateIssueStatus(jira, requestBody);
		} catch (final JiraTransitionNotFoundException e) {
			log.warn(e, e);
		}
	}

	public void updateTransitionScreenFieldsAndTransitionJiraStatus(
			final String jira, final Object fields, final String toStatus) {
		final Transition transition = this.jiraClient.getTransitions(jira).getTransitions().stream()
				.filter(t -> t.getToState().getName().equals(toStatus))
				.findFirst()
				.orElseThrow(
						() -> new IllegalArgumentException(
								"Could not transition to " + toStatus));

		final ObjectNode requestBody = this.objectMapper.createObjectNode();
		requestBody.putPOJO("fields", fields);
		requestBody.putPOJO("transition", Transition.builder().id(transition.getId()).build());
		this.jiraClient.updateIssueStatus(jira, requestBody);
	}

	public void setInterviewFieldsInJira(
			final String uuid, final InterviewServiceDeskIssue.Fields fields) {
		final String jira = this.jiraUUIDRepository
				.findByUuid(uuid)
				.orElse(JiraUUIDDAO.builder().build())
				.getJira();
		if (jira.isEmpty()) {
			throw new IllegalArgumentException("Jira issue not found");
		}
		final InterviewServiceDeskIssue interviewIssue = InterviewServiceDeskIssue.builder().fields(fields).build();
		this.jiraClient.setIssueFields(jira, interviewIssue);
	}

	public void setEvaluationFieldsInJira(
			final String uuid, final EvaluationServiceDeskIssue.Fields fields) {
		final String jiraKey = this.getJiraKeyFromUUID(uuid);
		final EvaluationServiceDeskIssue evaluationIssue = EvaluationServiceDeskIssue.builder().fields(fields).build();
		this.jiraClient.setIssueFields(jiraKey, evaluationIssue);
	}

	public void addCommentInJira(final String uuid, final JiraCommentDTO comment) {
		final String jira = this.jiraUUIDRepository
				.findByUuid(uuid)
				.orElse(JiraUUIDDAO.builder().build())
				.getJira();
		this.jiraClient.addComment(jira, comment);
	}

	public List<JiraCommentDTO> getJiraIssueComments(final String uuid) {
		final JiraUUIDDAO jiraUUIDDAO = this.jiraUUIDRepository.findByUuid(uuid).orElse(JiraUUIDDAO.builder().build());

		final GenericIssue issue = this.jiraClient.getGenericIssueV3(jiraUUIDDAO.getJira());

		return issue.getFields().getComment().getComments();
	}

	public JiraCommentDTO getJiraIssueComment(final String uuid, final Long commentId) {
		final JiraUUIDDAO jiraUUIDDAO = this.jiraUUIDRepository.findByUuid(uuid).orElse(JiraUUIDDAO.builder().build());
		return this.jiraClient.getJiraIssueComment(jiraUUIDDAO.getJira(), commentId);
	}

	public String getJiraStatusForInterview(final String jira) {
		return this.jiraClient.getInterview(jira).getFields().getStatus().getName();
	}

	public EvaluationServiceDeskIssue getEvaluationIssue(final String evaluationId) {
		final String jiraKey = this.getJiraKeyFromUUID(evaluationId);
		return this.jiraClient.getEvaluationServiceDeskIssue(jiraKey).getBody();
	}

	public String getJiraKeyFromUUID(final String uuid) {
		final String jiraKey = this.jiraUUIDRepository
				.findByUuid(uuid)
				.orElse(JiraUUIDDAO.builder().build())
				.getJira();
		if (jiraKey.isEmpty()) {
			throw new IllegalArgumentException("Jira issue not found");
		}
		return jiraKey;
	}

	public <T> CreateIssueResponse createIssue(final T issue) {
		return this.jiraClient.createIssue(issue);
	}

	public InterviewServiceDeskIssue getInterviewIssue(final String interviewId) {
		final String jiraKey = this.getJiraKeyFromUUID(interviewId);
		return this.jiraClient.getInterviewServiceDeskIssue(jiraKey);
	}
}
