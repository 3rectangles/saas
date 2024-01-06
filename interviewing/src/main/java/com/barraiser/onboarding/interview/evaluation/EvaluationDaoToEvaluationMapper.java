/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation;

import com.barraiser.common.graphql.types.Evaluation;
import com.barraiser.common.graphql.types.StatusType;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.StatusDAO;
import com.barraiser.onboarding.interview.evaluation.percentile.EvaluationPercentileFetcher;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Log4j2
public class EvaluationDaoToEvaluationMapper {
	private final EvaluationStatusManager evaluationStatusManager;

	public Evaluation toEvaluation(final EvaluationDAO evaluationDAO, final String algoVersion) {
		return this.toEvaluation(evaluationDAO).toBuilder().scoringAlgoVersion(algoVersion).build();
	}

	public Evaluation toEvaluation(final EvaluationDAO evaluationDAO) {
		final EvaluationDAO updatedEvaluationDAO = this.evaluationStatusManager.populateStatus(List.of(evaluationDAO))
				.get(0);
		return Evaluation.builder()
				.id(updatedEvaluationDAO.getId())
				.createdOn(updatedEvaluationDAO.getCreatedOn().getEpochSecond())
				.barraiserStatus(this.toStatusType(updatedEvaluationDAO.getBarraiserStatus()))
				.partnerStatus(this.toStatusType(updatedEvaluationDAO.getPartnerStatus()))
				.candidateId(updatedEvaluationDAO.getCandidateId())
				.scoringAlgoVersion(updatedEvaluationDAO.getDefaultScoringAlgoVersion())
				.pocEmail(updatedEvaluationDAO.getPocEmail())
				.percentile(EvaluationPercentileFetcher.getPercentileToDisplay(updatedEvaluationDAO))
				.build();
	}

	private StatusType toStatusType(final StatusDAO statusDAO) {
		if (statusDAO == null) {
			return StatusType.builder().build();
		}
		return StatusType.builder()
				.displayStatus(statusDAO.getDisplayStatus())
				.internalStatus(statusDAO.getInternalStatus())
				.transitionedOn(statusDAO.getCreatedOn().getEpochSecond())
				.build();
	}
}
