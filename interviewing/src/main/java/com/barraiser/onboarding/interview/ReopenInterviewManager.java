/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.scheduling.cancellation.ResetInterviewService;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Log4j2
@Component
@AllArgsConstructor
public class ReopenInterviewManager {
	public static final String SOURCE = "REOPEN_INTERVIEW";
	public static final String ACTION_PERFORMED_BY_SYSTEM = "SYSTEM";

	private final InterviewService interviewService;
	private final InterviewCreatorInJira interviewCreatorInJira;
	private final ResetInterviewService resetInterviewService;
	private final EvaluationStatusManager evaluationStatusManager;
	private final JiraWorkflowManager jiraWorkflowManager;
	private final JiraUUIDRepository jiraUUIDRepository;

	public void reopenInterview(final InterviewDAO interviewDAO, final String reopeningReasonId,
			final String reopenedBy) {
		this.updateInterviewInDB(interviewDAO, reopeningReasonId, reopenedBy);
		this.interviewCreatorInJira.createInterviewsInJira(Arrays.asList(interviewDAO),
				this.jiraUUIDRepository.findByUuid(interviewDAO.getEvaluationId()).get().getJira());
		this.updateEvaluationStatus(interviewDAO.getEvaluationId());
	}

	private void updateEvaluationStatus(final String evaluationId) {
		final EvaluationDAO evaluationDAO = this.evaluationStatusManager.getEvaluation(evaluationId);
		this.evaluationStatusManager.transitionBarRaiserStatus(evaluationDAO.getId(),
				EvaluationStatus.PENDING_SCHEDULING.getValue(), ACTION_PERFORMED_BY_SYSTEM);
		this.jiraWorkflowManager.transitionJiraStatus(evaluationDAO.getId(),
				EvaluationStatus.PENDING_SCHEDULING.getValue());
	}

	private void updateInterviewInDB(InterviewDAO interviewDAO, final String reopeningReasonId,
			final String reopenedBy) {
		interviewDAO = this.resetInterviewService.resetInterview(interviewDAO).toBuilder()
				.reopeningReasonId(reopeningReasonId).build();
		interviewDAO = interviewDAO.toBuilder().rescheduleCount(interviewDAO.getRescheduleCount() + 1).build();
		this.interviewService.save(interviewDAO, reopenedBy, SOURCE);
	}
}
