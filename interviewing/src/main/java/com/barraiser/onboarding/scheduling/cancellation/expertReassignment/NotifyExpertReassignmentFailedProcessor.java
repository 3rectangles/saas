/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment;

import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.JiraEvaluationStatusUpdatorForScheduling;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertReassignmentData;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.EvaluationServiceDeskIssue;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log4j2
@RequiredArgsConstructor
@Component
public class NotifyExpertReassignmentFailedProcessor implements ExpertReassignmentProcessor {
	private final JiraWorkflowManager jiraWorkflowManager;
	private final EvaluationStatusManager evaluationStatusManager;
	private final JiraEvaluationStatusUpdatorForScheduling jiraEvaluationStatusUpdatorForScheduling;
	private final InterViewRepository interViewRepository;

	@Override
	public void process(final ExpertReassignmentData data) throws Exception {
		this.unFlagInterviewForManualRescheduling(
				data.getInterview().getEvaluationId());
		data.setIsInterviewSuccessfullyRescheduled(
				this.isInterviewSuccessfullyRescheduled(
						data.getInterviewId()));
		if (Boolean.TRUE.equals(data.getIsInterviewSuccessfullyRescheduled())) {
			return;
		}
		this.updateEvaluationStatus(
				data.getInterview().getEvaluationId(),
				data.getIsCandidateSchedulingEnabled(), data.getInterviewId());
	}

	private void unFlagInterviewForManualRescheduling(final String evaluationId) {
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

	private void updateEvaluationStatus(
			final String evaluationId, final boolean isCandidateSchedulingOn, final String interviewId) {
		final EvaluationDAO evaluationDAO = this.evaluationStatusManager.getEvaluation(evaluationId);
		this.jiraEvaluationStatusUpdatorForScheduling.transition(
				evaluationDAO, null, isCandidateSchedulingOn, interviewId);
	}

	private Boolean isInterviewSuccessfullyRescheduled(final String interviewId) {
		final InterviewDAO interviewDAO = this.interViewRepository.findById(interviewId).get();
		return interviewDAO.getInterviewerId() != null;
	}
}
