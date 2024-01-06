/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.graphql.types.Evaluation;
import com.barraiser.common.graphql.types.StatusType;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.PartnerCompanyDAO;
import com.barraiser.onboarding.dal.PartnerCompanyRepository;
import com.barraiser.onboarding.dal.StatusDAO;
import com.barraiser.onboarding.interview.evaluation.percentile.EvaluationPercentileFetcher;
import com.barraiser.onboarding.interview.evaluation.scores.ScoreScaleConverter;
import com.barraiser.onboarding.interview.status.EvaluationStatusManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Component
@AllArgsConstructor
public class EvaluationMapper {
	private final EvaluationStatusManager evaluationStatusManager;
	private final ScoreScaleConverter scaleConverter;
	private final PartnerCompanyRepository partnerCompanyRepository;

	public List<Evaluation> mapEvaluationDAOsToEvaluations(
			final List<EvaluationDAO> evaluationDAOs) {
		final List<EvaluationDAO> updatedEvaluationDAOs = this.evaluationStatusManager.populateStatus(evaluationDAOs);
		final List<Evaluation> evaluations = new ArrayList<>();
		for (final EvaluationDAO evaluationDAO : updatedEvaluationDAOs) {

			final Evaluation evaluation = this.toEvaluation(evaluationDAO);
			evaluations.add(evaluation);
		}
		return evaluations;
	}

	public Evaluation toEvaluation(
			final EvaluationDAO evaluationDAO, final String scoringAlgoVersion) {
		final EvaluationDAO updatedEvaluation = this.evaluationStatusManager.populateStatus(List.of(evaluationDAO))
				.get(0);
		final StatusDAO barraiserStatus = updatedEvaluation.getBarraiserStatus() == null
				? StatusDAO.builder().build()
				: updatedEvaluation.getBarraiserStatus();
		final StatusDAO partnerStatus = updatedEvaluation.getPartnerStatus();
		Integer scaleBGS = scaleConverter.getScaleBGS(updatedEvaluation.getPartnerId());
		Integer scaleScoring = scaleConverter.getScaleScoring(updatedEvaluation.getPartnerId());
		final String version = scoringAlgoVersion != null
				? scoringAlgoVersion
				: updatedEvaluation.getDefaultScoringAlgoVersion();
		final Evaluation evaluation = Evaluation.builder()
				.id(updatedEvaluation.getId())
				.partnerId(updatedEvaluation.getPartnerId())
				.scaleBGS(scaleBGS)
				.scaleScoring(scaleScoring)
				.scoringAlgoVersion(version)
				.status(updatedEvaluation.getStatus())
				.barraiserStatus(this.getBarraiserStatus(updatedEvaluation, barraiserStatus))
				.partnerStatus(this.getPartnerStatus(updatedEvaluation, partnerStatus))
				.bgsCreatedTimeEpoch(this.evaluationStatusManager.getBgsCreatedTime(evaluationDAO.getId()))
				.jobRoleId(updatedEvaluation.getJobRoleId())
				.jobRoleVersion(updatedEvaluation.getJobRoleVersion())
				.candidateId(updatedEvaluation.getCandidateId())
				.createdOn(updatedEvaluation.getCreatedOn().toEpochMilli())
				.pocEmail(updatedEvaluation.getPocEmail())
				.waitingReasonId(updatedEvaluation.getWaitingReasonId())
				.cancellationReasonId(updatedEvaluation.getCancellationReasonId())
				.displayStatus(updatedEvaluation.getFinalStatus().getDisplayStatus())
				.percentile(
						EvaluationPercentileFetcher.getPercentileToDisplay(updatedEvaluation))
				.isEvaluationScoreUnderReview(updatedEvaluation.getIsEvaluationScoreUnderReview())
				.defaultScoringAlgoVersion(updatedEvaluation.getDefaultScoringAlgoVersion())
				.defaultRecommendationVersion(updatedEvaluation.getDefaultRecommendationVersion())
				.build();
		return evaluation;
	}

	public Evaluation toEvaluation(final EvaluationDAO evaluationDAO) {
		return this.toEvaluation(evaluationDAO, null);
	}

	private StatusType getBarraiserStatus(
			final EvaluationDAO evaluationDAO, final StatusDAO barraiserStatus) {
		return StatusType.builder()
				.id(barraiserStatus.getId())
				.internalStatus(barraiserStatus.getInternalStatus())
				.displayStatus(barraiserStatus.getDisplayStatus())
				.transitionedOn(
						this.evaluationStatusManager.getBarRaiserStatusTransitionedTime(
								evaluationDAO.getId(), evaluationDAO.getStatus()))
				.build();
	}

	private StatusType getPartnerStatus(
			final EvaluationDAO evaluationDAO, final StatusDAO partnerStatus) {
		return partnerStatus == null
				? null
				: StatusType.builder()
						.id(partnerStatus.getId())
						.internalStatus(partnerStatus.getInternalStatus())
						.displayStatus(partnerStatus.getDisplayStatus())
						.transitionedOn(
								this.evaluationStatusManager.getPartnerStatusTransitionedTime(
										evaluationDAO.getId(), partnerStatus.getId()))
						.build();
	}
}
