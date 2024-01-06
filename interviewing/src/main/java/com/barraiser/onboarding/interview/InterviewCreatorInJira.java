/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.common.IdNameField;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.JiraUUIDDAO;
import com.barraiser.onboarding.dal.JiraUUIDRepository;
import com.barraiser.onboarding.interview.jira.JiraUtil;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.CreateIssueResponse;
import com.barraiser.onboarding.interview.jira.dto.IdValueField;
import com.barraiser.onboarding.interview.jira.dto.InterviewServiceDeskIssue;
import com.barraiser.onboarding.interview.jira.interview.InterviewServiceDeskHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Component
public class InterviewCreatorInJira {
	private static final String INTERVIEW_ROUND_JIRA_PREFIX = "Round ";
	private static final String IS_PART_OF_EVALUATION_LINK_ID = "10007";
	private static final String PENDING_SCHEDULING_TRANISTION_ID = "151";

	@Qualifier("applicationEnvironment")
	private final String jiraSyncEnvironment;
	private final JiraUUIDRepository jiraUUIDRepository;
	private final JiraWorkflowManager jiraWorkflowManager;
	private final ObjectMapper objectMapper;
	private final JiraUtil jiraUtil;
	private final InterviewUtil interviewUtil;

	@Transactional
	public void createInterviewsInJira(final List<InterviewDAO> interviewDAOs, final String evaluationJiraKey) {
		int roundNumber = this.interviewUtil.getRoundNumberOfInterview(interviewDAOs.get(0));
		for (InterviewDAO interviewDAO : interviewDAOs) {
			final String jiraKey = this.createInterview(interviewDAO, evaluationJiraKey, roundNumber++);
			this.unlinkPreviousJira(interviewDAO.getId());
			this.addJiraUuid(interviewDAO, jiraKey);
		}
	}

	private String createInterview(final InterviewDAO interviewDAO, final String evaluationJiraKey,
			final Integer roundNumber) {
		final IdNameField issueType = IdNameField.builder()
				.id(InterviewServiceDeskHandler.JIRA_ISSUE_TYPE_ID_INTERVIEW_SERVICE_DESK).build();
		final IdNameField project = IdNameField.builder().id(JiraWorkflowManager.JIRA_PROJECT_ID_EVALUATION).build();

		final IdValueField interviewRound = IdValueField.builder().value(interviewDAO.getInterviewRound()).build();
		final IdValueField syncEnvironment = IdValueField.builder().value(this.jiraSyncEnvironment).build();

		final InterviewServiceDeskIssue.Fields fields = InterviewServiceDeskIssue.Fields.builder()
				.entityId(interviewDAO.getId())
				.issuetype(issueType)
				.project(project)
				.interviewStructureId(interviewDAO.getInterviewStructureId())
				.summary(INTERVIEW_ROUND_JIRA_PREFIX + roundNumber)
				.interviewRound(interviewRound)
				.syncEnvironment(syncEnvironment)
				.rescheduleCount(interviewDAO.getRescheduleCount().toString())
				.build();

		final CreateIssueResponse issueResponse = this.jiraWorkflowManager
				.createIssue(this.getCreateIssueBody(fields, evaluationJiraKey));

		return issueResponse.getKey();
	}

	private ObjectNode getCreateIssueBody(final InterviewServiceDeskIssue.Fields fields,
			final String evaluationJiraKey) {

		final ObjectNode requestBody = this.objectMapper.createObjectNode();
		requestBody.putPOJO("fields", fields);
		requestBody.putPOJO("update",
				this.jiraUtil.getIssueLinkUpdateBody(evaluationJiraKey, IS_PART_OF_EVALUATION_LINK_ID));
		requestBody.putPOJO("transition", IdNameField.builder().id(PENDING_SCHEDULING_TRANISTION_ID).build());

		return requestBody;
	}

	private void addJiraUuid(final InterviewDAO interviewDAO, final String jiraKey) {
		this.jiraUUIDRepository.save(JiraUUIDDAO.builder()
				.jira(jiraKey)
				.uuid(interviewDAO.getId())
				.build());
	}

	private void unlinkPreviousJira(final String interviewId) {
		final Optional<JiraUUIDDAO> jiraUUIDDAO = this.jiraUUIDRepository.findByUuid(interviewId);
		if (jiraUUIDDAO.isPresent()) {
			final InterviewServiceDeskIssue.Fields updatedFields = InterviewServiceDeskIssue.Fields.builder()
					.entityId(interviewId + "-unlinked")
					.build();
			this.jiraWorkflowManager.setInterviewFieldsInJira(interviewId, updatedFields);
			this.jiraUUIDRepository.deleteByUuid(interviewId);
		}
	}
}
