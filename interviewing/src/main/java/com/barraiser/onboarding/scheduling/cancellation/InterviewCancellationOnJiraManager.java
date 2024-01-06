/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.IdValueField;
import com.barraiser.onboarding.interview.jira.dto.InterviewServiceDeskIssue;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewCancellationOnJiraManager {
	private final CancellationReasonRepository cancellationReasonRepository;
	private final JiraWorkflowManager jiraWorkflowManager;
	private final DateUtils dateUtils;
	private final JiraUUIDRepository jiraUUIDRepository;

	public void cancelInterviewOnJira(final String interviewId, final String cancellationReasonId,
			final Long cancellationTime) {
		final JiraUUIDDAO jiraUUIDDAO = this.jiraUUIDRepository.findByUuid(interviewId).get();
		this.cancelInterviewOnJiraUsingJiraKey(jiraUUIDDAO.getJira(), cancellationReasonId, cancellationTime);
	}

	private String getFormattedCancellationReason(final String cancellationReasonId) {
		final CancellationReasonDAO cancellationReasonDAO = this.cancellationReasonRepository
				.findById(cancellationReasonId).get();
		final String formattedReason = cancellationReasonDAO.getCancellationType() + "--"
				+ cancellationReasonDAO.getCancellationReason() + ":" + cancellationReasonDAO.getId();
		return formattedReason;
	}

	public void cancelInterviewOnJiraUsingJiraKey(final String jiraKey, final String cancellationReasonId,
			final Long cancellationTime) {
		final OffsetDateTime cancellationDateTime = this.dateUtils.getDateTime(cancellationTime,
				DateUtils.TIMEZONE_ASIA_KOLKATA);

		final InterviewServiceDeskIssue.Fields fields = InterviewServiceDeskIssue.Fields.builder()
				.cancellationReason(
						IdValueField.builder().value(this.getFormattedCancellationReason(cancellationReasonId)).build())
				.cancellationTime(cancellationDateTime)
				.build();

		this.jiraWorkflowManager.updateTransitionScreenFieldsAndTransitionJiraStatus(jiraKey, fields,
				InterviewStatus.CANCELLATION_DONE.getValue());
	}
}
