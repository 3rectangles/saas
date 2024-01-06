/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.dal.InterviewStatus;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@AllArgsConstructor
@Component
public class InterviewUpdationOnJiraForCancellationProcessor implements CancellationProcessor {
	private final JiraWorkflowManager jiraWorkflowManager;
	private final InterviewCancellationOnJiraManager interviewCancellationOnJiraManager;

	@Override
	public void process(final CancellationProcessingData data) throws JsonProcessingException {

		final String jiraKey = data.getOldJiraKey();
		final Boolean isInterviewAlreadyCancelled = InterviewStatus.CANCELLATION_DONE.getValue()
				.equalsIgnoreCase(this.jiraWorkflowManager.getJiraStatusForInterview(jiraKey));

		if (!isInterviewAlreadyCancelled) {
			this.interviewCancellationOnJiraManager.cancelInterviewOnJiraUsingJiraKey(
					jiraKey,
					data.getPreviousStateOfCancelledInterview().getCancellationReasonId(),
					Long.parseLong(data.getPreviousStateOfCancelledInterview().getCancellationTime()));
		}
	}
}
