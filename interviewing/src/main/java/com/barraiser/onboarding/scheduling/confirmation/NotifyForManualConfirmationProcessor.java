/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.confirmation;

import com.barraiser.onboarding.scheduling.confirmation.dto.InterviewConfirmationLifecycleDTO;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.InterviewServiceDeskIssue;
import com.barraiser.onboarding.interview.jira.dto.JiraCommentDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class NotifyForManualConfirmationProcessor implements ConfirmationProcessor {
	public static final String JIRA_COMMENT_FOR_INTERVIEW_CONFIRMATION = "Team, Please take the confirmation on " +
			"this link : %s";
	public static final String MANUAL_CONFIRMATION_LINK = "https://app.barraiser.com/interview-confirmation/candidate/%s?source=manual";
	private final JiraWorkflowManager jiraWorkflowManager;

	@Override
	public void process(final InterviewConfirmationLifecycleDTO data) throws Exception {
		this.flagInterviewForManualConfirmation(data.getInterviewId());
		this.addCommentOnJiraForManualConfirmation(data.getInterviewId());
	}

	private void flagInterviewForManualConfirmation(final String interviewId) {
		final InterviewServiceDeskIssue currentIssue = this.jiraWorkflowManager.getInterviewIssue(interviewId);
		final List<String> priorityFlags = Objects.requireNonNullElse(currentIssue.getFields().getPriorityFlags(),
				new ArrayList<>());
		if (!priorityFlags.contains(ConfirmationConstants.CALL_CANDIDATE_FOR_CONFIRMATION_PRIORITY_FLAG)) {
			priorityFlags.add(ConfirmationConstants.CALL_CANDIDATE_FOR_CONFIRMATION_PRIORITY_FLAG);
		}
		final InterviewServiceDeskIssue.Fields updatedFields = InterviewServiceDeskIssue.Fields.builder()
				.priorityFlags(priorityFlags)
				.build();
		this.jiraWorkflowManager.setInterviewFieldsInJira(interviewId, updatedFields);
	}

	private void addCommentOnJiraForManualConfirmation(final String interviewId) {
		final String manualConfirmationLink = String.format(MANUAL_CONFIRMATION_LINK, interviewId);
		final String comment = String.format(
				JIRA_COMMENT_FOR_INTERVIEW_CONFIRMATION, manualConfirmationLink);
		final JiraCommentDTO jiraCommentDTO = JiraCommentDTO.builder().body(comment).build();
		this.jiraWorkflowManager.addCommentInJira(interviewId, jiraCommentDTO);
	}
}
