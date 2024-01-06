/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.drop_candidature;

import com.barraiser.commons.auth.AuthenticatedUser;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.jira.JiraWorkflowManager;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import com.barraiser.onboarding.partner.EvaluationManager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class DropCandidatureEvaluationManager {

	private final InterViewRepository interViewRepository;
	private final EvaluationRepository evaluationRepository;
	private final EvaluationManager evaluationManager;
	private final EvaluationStatusManager evaluationStatusManager;
	private final JiraWorkflowManager jiraWorkflowManager;

	public static final String INTERNAL_STATUS_CANCELLED = "cancelled";

	public void updateEvaluation(final AuthenticatedUser actor, final String evaluationId,
			final String cancellationReasonId) {

		final List<InterviewDAO> interviews = this.interViewRepository.findAllByEvaluationId(evaluationId);

		final String brEvaluationStatus = this.getFinalBREvaluationStatus(interviews);
		this.updateBRStatus(actor, evaluationId, brEvaluationStatus);

		if (EvaluationStatus.CANCELLED.equals(EvaluationStatus.fromString(brEvaluationStatus))) {
			this.saveCancellationReason(evaluationId, cancellationReasonId);
		}

		this.markPartnerStatusCancelled(evaluationId, actor.getUserName());
	}

	/**
	 * NOTE : We have enabled cancel and done transition from all states in jira .
	 * For all newly added transitions for this purpose we have put a conditional
	 * check
	 * to allow such transitions only for automation@barraiser.com user.
	 */
	private void updateBRStatus(final AuthenticatedUser actor, final String evaluationId,
			final String brEvaluationStatus) {
		this.evaluationStatusManager.transitionBarRaiserStatus(evaluationId,
				brEvaluationStatus,
				actor.getUserName());

		this.jiraWorkflowManager.transitionJiraStatus(evaluationId, brEvaluationStatus);

	}

	private String getFinalBREvaluationStatus(List<InterviewDAO> interviews) {
		if (this.isAnyInterviewSuccessfullyCompleted(interviews)) {
			return EvaluationStatus.DONE.getValue();
		} else {
			return EvaluationStatus.CANCELLED.getValue();
		}
	}

	private void saveCancellationReason(final String evaluationId, final String cancellationReasonId) {
		final EvaluationDAO evaluationDAO = this.evaluationRepository.findById(evaluationId).get();
		this.evaluationRepository
				.save(evaluationDAO.toBuilder().cancellationReasonId(cancellationReasonId).build());
	}

	private Boolean areAllInterviewsConcluded(final List<InterviewDAO> interviews) {
		final List<InterviewDAO> concludedInterviews = interviews.stream()
				.filter(x -> InterviewStatus.DONE.equals(InterviewStatus.fromString(x.getStatus()))
						|| InterviewStatus.CANCELLATION_DONE.equals(InterviewStatus.fromString(x.getStatus())))
				.collect(Collectors.toList());
		return concludedInterviews.size() == interviews.size();
	}

	private Boolean isAnyInterviewSuccessfullyCompleted(final List<InterviewDAO> interviews) {
		final List<InterviewDAO> concludedInterviews = interviews.stream()
				.filter(x -> InterviewStatus.DONE.equals(InterviewStatus.fromString(x.getStatus())))
				.collect(Collectors.toList());
		return concludedInterviews.size() != 0;
	}

	private void markPartnerStatusCancelled(final String evaluationId, final String userId) {
		final String partnerId = this.evaluationManager.getPartnerCompanyForEvaluation(evaluationId);
		final StatusDAO rejectedStatus = this.evaluationStatusManager.getAllStatusForPartner(partnerId).stream()
				.filter(s -> !EvaluationStatusManager.BARRAISER_PARTNER_ID.equals(s.getPartnerId())
						&& INTERNAL_STATUS_CANCELLED.equals(s.getInternalStatus()))
				.findFirst().get();
		this.evaluationStatusManager.transitionPartnerStatus(evaluationId, rejectedStatus.getId(), userId);
	}

}
