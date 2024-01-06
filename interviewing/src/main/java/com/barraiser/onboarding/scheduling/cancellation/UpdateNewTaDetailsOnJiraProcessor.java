/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.interview.InterviewStatusManager;
import com.barraiser.onboarding.interview.jira.JiraUtil;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.IdValueField;
import com.barraiser.onboarding.interview.jira.dto.InterviewServiceDeskIssue;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Log4j2
@Component
@AllArgsConstructor
public class UpdateNewTaDetailsOnJiraProcessor implements CancellationProcessor {
	private final JiraWorkflowManager jiraWorkflowManager;
	private final JiraUtil jiraUtil;
	private final InterviewStatusManager interviewStatusManager;

	@Override
	public void process(final CancellationProcessingData data) {
		if (!data.getIsTaAutoAllocationEnabled() || Objects.isNull(data.getInterviewForTaReassignment()))
			return;
		this.updateDetailsOnJira(
				data.getInterviewForTaReassignment(),
				data.getPreviousStateOfCancelledInterview().getTaggingAgent());
	}

	private void updateDetailsOnJira(final InterviewDAO interviewDAO, final String taggingAgent) {
		final InterviewServiceDeskIssue.Fields updatedFields = InterviewServiceDeskIssue.Fields.builder()
				.taggingAgent(IdValueField.builder().value(jiraUtil.getTaggingAgentValueForJira(taggingAgent)).build())
				.build();
		this.moveJiraToPendingInterview(interviewDAO);
		this.jiraWorkflowManager.setInterviewFieldsInJira(interviewDAO.getId(), updatedFields);
	}

	private void moveJiraToPendingInterview(final InterviewDAO interviewDAO) {
		this.interviewStatusManager.updateInterviewStatus(interviewDAO,
				InterviewStatus.PENDING_INTERVIEWING, null, null);
	}
}
