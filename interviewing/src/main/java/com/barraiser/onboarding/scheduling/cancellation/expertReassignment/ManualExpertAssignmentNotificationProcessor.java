/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment;

import com.barraiser.common.utilities.DateUtils;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.CancellationReasonManager;
import com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO.ExpertReassignmentData;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.EvaluationServiceDeskIssue;
import com.barraiser.onboarding.interview.jira.dto.JiraCommentDTO;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log4j2
@Component
@AllArgsConstructor
public class ManualExpertAssignmentNotificationProcessor implements ExpertReassignmentProcessor {
	public static final String PRIORITY_FLAG = "expert_assignment_required";
	public static final String EXPERT_ALLOCATION_LINK = "https://app.barraiser.com/expert-allocation?interview=%s";
	public static final String JIRA_COMMENT_FOR_EXPERT_ALLOCATION = "Team, Please try scheduling this interview for : %s - %s using %s";
	public static final String JIRA_COMMENT_FOR_EXPERT_CANCELLATION_REASON = "The interview was cancelled by the previous expert stating %s : %s";

	private final JiraWorkflowManager jiraWorkflowManager;
	private final DateUtils dateUtils;
	private final EvaluationStatusManager evaluationStatusManager;
	private final EvaluationRepository evaluationRepository;
	private final CancellationReasonManager cancellationReasonManager;

	@Override
	public void process(final ExpertReassignmentData data) throws Exception {
		final InterviewDAO cancelledInterview = data.getInterview();
		this.flagInterviewForManualRescheduling(cancelledInterview.getEvaluationId());
		this.addCancellationReasonCommentOnJira(data.getInterviewId(), data.getReassignmentReason());
		this.addCommentForManualExpertReassignmentOnJira(
				data.getInterviewId(),
				cancelledInterview.getStartDate(),
				cancelledInterview.getEndDate());
		this.transitionEvaluationStatus(cancelledInterview);
	}

	private void flagInterviewForManualRescheduling(final String evaluationId) {
		final EvaluationServiceDeskIssue currentIssue = this.jiraWorkflowManager.getEvaluationIssue(evaluationId);
		final List<String> priorityFlags = Objects.requireNonNullElse(
				currentIssue.getFields().getPriorityFlags(), new ArrayList<>());
		if (!priorityFlags.contains(PRIORITY_FLAG)) {
			priorityFlags.add(PRIORITY_FLAG);
		}
		final EvaluationServiceDeskIssue.Fields updatedFields = EvaluationServiceDeskIssue.Fields.builder()
				.priorityFlags(priorityFlags).build();

		this.jiraWorkflowManager.setEvaluationFieldsInJira(evaluationId, updatedFields);
	}

	private void addCommentForManualExpertReassignmentOnJira(
			final String interviewId, final Long startDate, final Long endDate) {
		final String startTimeOfInterviewInIST = this.dateUtils.getFormattedDateString(
				startDate, null, DateUtils.TIME_IN_12_HOUR_FORMAT);
		final String endTimeOfInterviewInIST = this.dateUtils.getFormattedDateString(
				endDate, null, DateUtils.TIME_IN_12_HOUR_FORMAT);

		final String expertAllocationLink = String.format(EXPERT_ALLOCATION_LINK, interviewId);

		final String comment = String.format(
				JIRA_COMMENT_FOR_EXPERT_ALLOCATION,
				startTimeOfInterviewInIST,
				endTimeOfInterviewInIST, expertAllocationLink);

		final JiraCommentDTO jiraCommentDTO = JiraCommentDTO.builder().body(comment).build();

		this.jiraWorkflowManager.addCommentInJira(interviewId, jiraCommentDTO);
	}

	private void transitionEvaluationStatus(final InterviewDAO interviewDAO) {
		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(interviewDAO.getEvaluationId()).get();
		if (!List.of(EvaluationStatus.PENDING_SCHEDULING.getValue(), EvaluationStatus.CANCELLED.getValue())
				.contains(evaluationDAO.getStatus())) {
			this.evaluationStatusManager.transitionBarRaiserStatus(
					evaluationDAO.getId(), EvaluationStatus.PENDING_SCHEDULING.getValue(), null);
			this.jiraWorkflowManager.transitionJiraStatus(
					evaluationDAO.getId(), EvaluationStatus.PENDING_SCHEDULING.getValue());
		}
	}

	private void addCancellationReasonCommentOnJira(final String interviewId, final String reassignmentReason) {
		final CancellationReasonDAO cancellationReasonDAO = this.cancellationReasonManager
				.getCancellationReasonForId(reassignmentReason);
		final String comment = String.format(
				JIRA_COMMENT_FOR_EXPERT_CANCELLATION_REASON, cancellationReasonDAO.getId(),
				cancellationReasonDAO.getCancellationReason());

		final JiraCommentDTO jiraCommentDTO = JiraCommentDTO.builder().body(comment).build();

		this.jiraWorkflowManager.addCommentInJira(interviewId, jiraCommentDTO);
	}
}
