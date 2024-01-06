/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.evaluation;

import com.barraiser.common.utilities.EmailParser;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.interview.jira.JiraUtil;
import com.barraiser.onboarding.interview.jira.dto.EvaluationServiceDeskIssue.Fields;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;

@Component
@AllArgsConstructor
public class EvaluationFieldUpdator {
	public static final String STATUS_DONE = "Done";
	private final EvaluationRepository evaluationRepository;
	private final JiraUtil jiraUtil;

	public EvaluationDAO update(final Fields fields, final EvaluationDAO originalEvaluation, final String candidateId) {
		final String waitingReasonId = fields.getWaitingReason() == null
				? null
				: this.jiraUtil.getIdFromString(fields.getWaitingReason().getValue());
		final String cancellationReasonId = fields.getCancellationReason() == null
				? originalEvaluation.getCancellationReasonId()
				: this.jiraUtil.getIdFromString(fields.getCancellationReason().getValue());

		final Boolean isCandidateSchedulingBlocked = fields.getPriorityFlags() != null
				? fields.getPriorityFlags().contains("candidate_scheduling_blocked")
				: null;

		EvaluationDAO evaluationToSave = originalEvaluation.toBuilder()
				.candidateId(candidateId)
				.waitingReasonId(waitingReasonId)
				.cancellationReasonId(cancellationReasonId)
				.pocEmail(this.getPocEmail(fields))
				.blockCandidateScheduling(isCandidateSchedulingBlocked)
				.build();

		if (this.hasTransitionedToDone(fields, originalEvaluation)) {
			evaluationToSave = evaluationToSave.toBuilder().bgsCreatedTimeEpoch(Instant.now().getEpochSecond()).build();
		}
		this.evaluationRepository.save(evaluationToSave);
		return evaluationToSave;
	}

	private String getPocEmail(final Fields fields) {
		String pocEmail = fields.getPocEmail();
		if (pocEmail != null) {
			pocEmail = pocEmail.trim();
			Arrays.stream(fields.getPocEmail().split(","))
					.map(String::trim)
					.forEach(EmailParser::validateEmail);
		}
		return pocEmail;
	}

	private boolean hasTransitionedToDone(final Fields fields, final EvaluationDAO evaluation) {
		return fields.getStatus().getName().equals(STATUS_DONE)
				&& !STATUS_DONE.equals(evaluation.getStatus());
	}
}
