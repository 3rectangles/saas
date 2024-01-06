/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.followup;

import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.EvaluationServiceDeskIssue;
import com.barraiser.onboarding.sfn.StepFunctionProcessor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@AllArgsConstructor
public class CallCandidateManuallyForScheduling implements StepFunctionProcessor<FollowUpForSchedulingStepFunctionDTO> {

	private final JiraWorkflowManager jiraWorkflowManager;

	@Override
	public String getFlowIdentifier(FollowUpForSchedulingStepFunctionDTO data) {
		return data.getEvaluationId();
	}

	@Override
	public void process(FollowUpForSchedulingStepFunctionDTO data) throws Exception {
		String evaluationId = data.getEvaluationId();
		final EvaluationServiceDeskIssue currentIssue = this.jiraWorkflowManager.getEvaluationIssue(evaluationId);
		final List<String> priorityFlags = Objects.requireNonNullElse(currentIssue.getFields().getPriorityFlags(),
				new ArrayList<>());
		if (!priorityFlags.contains(FollowUpConstants.CALL_CANDIDATE_FOR_SCHEDULING)) {
			priorityFlags.add(FollowUpConstants.CALL_CANDIDATE_FOR_SCHEDULING);
		}
		final EvaluationServiceDeskIssue.Fields updatedFields = EvaluationServiceDeskIssue.Fields.builder()
				.priorityFlags(priorityFlags)
				.build();
		this.jiraWorkflowManager.setEvaluationFieldsInJira(evaluationId, updatedFields);
	}
}
