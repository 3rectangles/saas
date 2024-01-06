/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.jira.dto.JiraCommentDTO;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import com.barraiser.onboarding.partner.EvaluationManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@RequiredArgsConstructor
@Component
public class InterviewRejectionProcessor {
	private static final String COMMENT_FOR_PARTNER_ABOUT_CANDIDATE_REJECTION_BY_PARTNER = "Candidate rejected by %s";
	private static final String COMMENT_FOR_PARTNER_ABOUT_CANDIDATE_REJECTION_BY_BARRAISER = "Candidate rejected - Couldn’t clear the cutoff score";
	public static final String INTERNAL_STATUS_REJECTED = "rejected";
	public static final String SOURCE_BARRAISER_REJECTED = "barraiser_rejected";
	public static final String SOURCE_PARTNER_REJECTED = "partner_rejected";

	private final EvaluationStatusManager evaluationStatusManager;
	private final JiraWorkflowManager jiraWorkflowManager;
	private final EvaluationManager evaluationManager;
	private final InterviewUtil interviewUtil;

	public void rejectInterview(final String interviewId, final AuthenticatedUser user, final String source) {
		EvaluationDAO evaluationDAO = this.interviewUtil.getEvaluationForInterview(interviewId);
		evaluationDAO = this.evaluationStatusManager.transitionBarRaiserStatus(evaluationDAO.getId(),
				EvaluationStatus.DONE.getValue(), user.getUserName());
		final String commentOnPartnerPortal = this.getCommentForPartner(source, user.getEmail());
		this.takeActionOnJira(evaluationDAO, commentOnPartnerPortal);
		this.transitionPartnerStatus(evaluationDAO, user.getUserName());
	}

	private void takeActionOnJira(final EvaluationDAO evaluation, final String comment) {
		this.jiraWorkflowManager.transitionJiraStatus(evaluation.getId(), EvaluationStatus.DONE.getValue());
		this.jiraWorkflowManager.addCommentInJira(evaluation.getId(), JiraCommentDTO.builder().body(comment).build());
	}

	private void transitionPartnerStatus(final EvaluationDAO evaluationDAO, final String userId) {
		final String partnerId = this.evaluationManager.getPartnerCompanyForEvaluation(evaluationDAO.getId());
		final StatusDAO rejectedStatus = this.evaluationStatusManager.getAllStatusForPartner(partnerId).stream()
				.filter(s -> !EvaluationStatusManager.BARRAISER_PARTNER_ID.equals(s.getPartnerId())
						&& INTERNAL_STATUS_REJECTED.equals(s.getInternalStatus()))
				.findFirst().get();
		this.evaluationStatusManager.transitionPartnerStatus(evaluationDAO.getId(), rejectedStatus.getId(), userId);
	}

	private String getCommentForPartner(final String source, final String userEmail) {
		if (SOURCE_PARTNER_REJECTED.equals(source)) {
			return String.format(COMMENT_FOR_PARTNER_ABOUT_CANDIDATE_REJECTION_BY_PARTNER, userEmail);
		} else {
			return COMMENT_FOR_PARTNER_ABOUT_CANDIDATE_REJECTION_BY_BARRAISER;
		}
	}

}
