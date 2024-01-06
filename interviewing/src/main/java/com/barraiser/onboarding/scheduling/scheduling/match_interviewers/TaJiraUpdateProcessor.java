/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.interview.InterviewService;
import com.barraiser.onboarding.interview.InterviewStatusManager;
import com.barraiser.onboarding.interview.jira.JiraUtil;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.IdValueField;
import com.barraiser.onboarding.interview.jira.dto.InterviewServiceDeskIssue;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class TaJiraUpdateProcessor implements SchedulingProcessor {

	private final JiraWorkflowManager jiraWorkflowManager;
	private final JiraUtil jiraUtil;
	private final InterviewStatusManager interviewStatusManager;
	private final InterviewService interviewService;

	@Override
	public void process(final SchedulingProcessingData data) {
		if (!data.getExecuteTaAssignment() || !data.getIsTaAllocated())
			return;
		final InterviewServiceDeskIssue.Fields updatedFields = InterviewServiceDeskIssue.Fields.builder()
				.taggingAgent(
						IdValueField.builder().value(jiraUtil.getTaggingAgentValueForJira(data.getTaId())).build())
				.build();
		this.moveJiraToPendingInterview(data.getInput().getInterviewId());
		this.jiraWorkflowManager.setInterviewFieldsInJira(data.getInput().getInterviewId(), updatedFields);
	}

	private void moveJiraToPendingInterview(final String interviewId) {
		final InterviewDAO interviewDAO = this.interviewService.findById(interviewId);
		this.interviewStatusManager.updateInterviewStatus(interviewDAO,
				InterviewStatus.PENDING_INTERVIEWING, null, null);
	}

}
