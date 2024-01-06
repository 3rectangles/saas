/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment.sfn_activities;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.UserDetailsDAO;
import com.barraiser.onboarding.dal.UserDetailsRepository;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertAllocatorData;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.ManualExpertAssignmentNotificationProcessor;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.EvaluationServiceDeskIssue;
import com.barraiser.onboarding.interview.jira.dto.JiraCommentDTO;
import com.barraiser.onboarding.scheduling.JiraCommentContentCreator;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log4j2
@Component
@AllArgsConstructor
public class UpdateInterviewOnJiraForExpertAllocationProcessor implements ExpertAllocatorSfnActivity {
	public static final String UPDATE_INTERVIEW_ON_JIRA_ACTIVITY_NAME = "update-interview-on-jira-for-expert-allocation";

	private final UserDetailsRepository userDetailsRepository;
	private final JiraCommentContentCreator jiraCommentContentCreator;
	private final JiraWorkflowManager jiraWorkflowManager;
	private final ObjectMapper objectMapper;

	@Override
	public String name() {
		return UPDATE_INTERVIEW_ON_JIRA_ACTIVITY_NAME;
	}

	@Override
	public ExpertAllocatorData process(final String input) throws IOException {
		final ExpertAllocatorData data = objectMapper.readValue(input, ExpertAllocatorData.class);
		this.updateDetailsOnJira(data.getInterview(), data.getInterviewerId(), data.getIsExpertDuplicate());
		this.removePriorityFlag(data.getInterview().getEvaluationId());
		return data;
	}

	private void updateDetailsOnJira(final InterviewDAO interviewDAO, final String interviewerId,
			final Boolean isExpertDuplicate) {
		final UserDetailsDAO interviewer = this.userDetailsRepository.findById(interviewerId).get();
		final JiraCommentDTO comment = JiraCommentDTO.builder()
				.body(
						this.jiraCommentContentCreator
								.createInterviewerDetailsJiraCommentContent(
										interviewDAO, interviewer, isExpertDuplicate))
				.build();
		this.jiraWorkflowManager.addCommentInJira(interviewDAO.getId(), comment);
	}

	private void removePriorityFlag(final String evaluationId) {
		final EvaluationServiceDeskIssue currentIssue = this.jiraWorkflowManager.getEvaluationIssue(evaluationId);
		final List<String> priorityFlags = Objects.requireNonNullElse(
				currentIssue.getFields().getPriorityFlags(), new ArrayList<>());
		if (priorityFlags.contains(ManualExpertAssignmentNotificationProcessor.PRIORITY_FLAG)) {
			priorityFlags.remove(ManualExpertAssignmentNotificationProcessor.PRIORITY_FLAG);
		}
		final EvaluationServiceDeskIssue.Fields updatedFields = EvaluationServiceDeskIssue.Fields.builder()
				.priorityFlags(priorityFlags).build();

		this.jiraWorkflowManager.setEvaluationFieldsInJira(evaluationId, updatedFields);
	}
}
