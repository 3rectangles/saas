/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterviewService;
import com.barraiser.onboarding.interview.jira.JiraUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class InterviewCreationInDatabaseProcessor implements CancellationProcessor {
	private final InterviewService interviewService;
	private final JiraUtil jiraUtil;
	private final ResetInterviewService resetInterviewService;

	@Override
	public void process(final CancellationProcessingData data) {
		final String jiraKey = this.jiraUtil.getEntityJiraKey(data.getInterviewToBeCancelled().getId());
		data.setOldJiraKey(jiraKey);
		if (!data.getIsNonReschedulableInterview()) {
			this.createNewInterviewInDatabase(data);
		}
	}

	private void createNewInterviewInDatabase(final CancellationProcessingData data) {
		final String createdBy = "Barraiser";
		final Integer rescheduleCount = data.getInterviewToBeCancelled().getRescheduleCount() + 1;
		final String rescheduledFrom = Boolean.TRUE.equals(data.getInterviewToBeCancelled().getIsRescheduled())
				? data.getOldJiraKey()
				: null;

		InterviewDAO interviewDAO = this.resetInterviewService.resetInterview(data.getInterviewToBeCancelled())
				.toBuilder()
				.rescheduleCount(rescheduleCount)
				.rescheduledFrom(rescheduledFrom)
				.build();

		interviewDAO = this.interviewService.save(interviewDAO, createdBy);
		data.setInterviewToBeCancelled(interviewDAO);
	}
}
