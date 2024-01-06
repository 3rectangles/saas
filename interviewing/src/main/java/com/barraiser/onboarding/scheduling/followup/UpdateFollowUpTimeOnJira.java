/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.followup;

import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.EvaluationServiceDeskIssue;
import com.barraiser.onboarding.scheduling.followup.FollowUpForSchedulingStepFunctionDTO;
import com.barraiser.onboarding.scheduling.followup.GetAdjustedFollowUpTime;
import com.barraiser.onboarding.sfn.StepFunctionProcessor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Component
@AllArgsConstructor
public class UpdateFollowUpTimeOnJira implements StepFunctionProcessor<FollowUpForSchedulingStepFunctionDTO> {

	private final JiraWorkflowManager jiraWorkflowManager;
	private final static Integer updated_follow_up_time_in_hrs = 4;
	private final GetAdjustedFollowUpTime getAdjustedFollowUpTime;

	@Override
	public String getFlowIdentifier(FollowUpForSchedulingStepFunctionDTO data) {
		return data.getEvaluationId();
	}

	@Override
	public void process(FollowUpForSchedulingStepFunctionDTO data) throws Exception {
		final String evaluationId = data.getEvaluationId();
		final long followUpDateAdjustedInEpoch = this.getAdjustedFollowUpTime.findTimeXMinsAfterExcludingNonOpHrs(
				(updated_follow_up_time_in_hrs * 60), Instant.now().getEpochSecond());
		final OffsetDateTime followUpDateOffsetDateTime = OffsetDateTime
				.ofInstant(Instant.ofEpochSecond(followUpDateAdjustedInEpoch), ZoneId.of("Asia/Calcutta"));
		final EvaluationServiceDeskIssue.Fields updatedFields = EvaluationServiceDeskIssue.Fields.builder()
				.followUpDate(followUpDateOffsetDateTime)
				.build();
		this.jiraWorkflowManager.setEvaluationFieldsInJira(evaluationId, updatedFields);
	}
}
