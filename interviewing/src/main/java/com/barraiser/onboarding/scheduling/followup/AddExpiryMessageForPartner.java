/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.followup;

import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationStatus;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.JiraCommentDTO;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import com.barraiser.onboarding.sfn.StepFunctionProcessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class AddExpiryMessageForPartner implements StepFunctionProcessor<FollowUpForSchedulingStepFunctionDTO> {

	public static final String COMMENT_FOR_FOLLOW_UP_EXPIRY_TIME_REACHED = "Candidate has not scheduled the interview(s) even after multiple follow-ups. Kindly connect with the candidate and schedule the interview(s)";

	private final EvaluationStatusManager evaluationStatusManager;
	private final JiraWorkflowManager jiraWorkflowManager;
	private final ObjectMapper objectMapper;

	@Override
	public String getFlowIdentifier(FollowUpForSchedulingStepFunctionDTO data) {
		return data.getEvaluationId();
	}

	@Override
	public void process(FollowUpForSchedulingStepFunctionDTO data) throws Exception {
		final EvaluationDAO evaluationDAO = this.objectMapper.convertValue(data.getEvaluation(), EvaluationDAO.class);
		this.evaluationStatusManager.transitionBarRaiserStatus(evaluationDAO.getId(),
				EvaluationStatus.WAITING_CLIENT.getValue(),
				EvaluationStatusManager.BARRAISER_PARTNER_ID);
		this.jiraWorkflowManager.transitionJiraStatus(evaluationDAO.getId(),
				EvaluationStatus.WAITING_CLIENT.getValue());
		this.jiraWorkflowManager.addCommentInJira(evaluationDAO.getId(),
				JiraCommentDTO.builder().body(COMMENT_FOR_FOLLOW_UP_EXPIRY_TIME_REACHED).build());
	}
}
