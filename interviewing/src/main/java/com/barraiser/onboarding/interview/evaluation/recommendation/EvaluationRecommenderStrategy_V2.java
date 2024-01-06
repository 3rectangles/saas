/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.recommendation;

import com.barraiser.common.graphql.types.EvaluationRecommendation;
import com.barraiser.common.graphql.types.RecommendationType;
import com.barraiser.onboarding.common.Constants;
import com.barraiser.onboarding.config.DynamicAppConfigProperties;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.evaluation.enums.InterviewProcessType;
import com.barraiser.onboarding.interview.evaluation.scores.BgsScoreFetcher;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class EvaluationRecommenderStrategy_V2 implements EvaluationRecommenderStrategy {
	private static final String RECOMMENDATION_ALGO_VERSION = "2";
	private static final String DEFAULT_CUTOFF_SCORE = "default-cutoff-score-for-evaluation-recommendation-v2";

	private final InterViewRepository interViewRepository;
	private final JobRoleToInterviewStructureRepository jobRoleToInterviewStructureRepository;
	private final EvaluationRepository evaluationRepository;
	private final BgsScoreFetcher bgsScoreFetcher;
	private final DynamicAppConfigProperties appConfigProperties;
	private final EvaluationRecommendationRepository evaluationRecommendationRepository;

	private static final Integer MAX_BGS_SCORE = 800;
	private static final Double CONSTANT_FOR_STRONGLY_RECOMMENDED = 0.3;
	private static final Double CONSTANT_FOR_RECOMMENDED = 0D;

	@Override
	public String version() {
		return RECOMMENDATION_ALGO_VERSION;
	}

	@Override
	public EvaluationRecommendation getRecommendation(final String evaluationId) {
		final EvaluationDAO evaluationDAO = this.evaluationRepository
				.findById(evaluationId)
				.get();

		if (this.isRecommendationAlreadyCalculated(evaluationId)) {
			return null;
		}

		final List<JobRoleToInterviewStructureDAO> jobRoleToInterviewStructureDAOsForBRRounds = this
				.getJobRoleToInterviewStructureForBarRaiserInterviewRound(evaluationDAO);

		if (jobRoleToInterviewStructureDAOsForBRRounds.size() == 0) {
			log.info("Not generating recommendations as no BR round configured for job role");
			return null;
		}

		if (!this.areAllBRRoundsCompletedForJobRole(
				evaluationDAO,
				jobRoleToInterviewStructureDAOsForBRRounds)) {
			return null;
		}

		final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureForLastBRInterviewRound = jobRoleToInterviewStructureDAOsForBRRounds
				.get(jobRoleToInterviewStructureDAOsForBRRounds.size() - 1);

		return this.getRecommendation(
				evaluationDAO,
				jobRoleToInterviewStructureForLastBRInterviewRound);
	}

	// todo: variable evaluation Id is not used
	@Override
	public EvaluationRecommendation getRecommendation(final InterviewDAO interviewDAO, final String evaluationId) {
		final EvaluationDAO evaluationDAO = this.evaluationRepository
				.findById(interviewDAO.getEvaluationId())
				.get();

		final List<JobRoleToInterviewStructureDAO> jobRoleToInterviewStructureDAOsForBRRounds = this
				.getJobRoleToInterviewStructureForBarRaiserInterviewRound(evaluationDAO);

		if (jobRoleToInterviewStructureDAOsForBRRounds.size() == 0) {
			log.info("Not generating recommendations as no BR round configured for job role");
			return null;
		}

		if (!this.areAllBRRoundsCompletedForJobRoleExceptForCurrentInterview(
				evaluationDAO,
				jobRoleToInterviewStructureDAOsForBRRounds, interviewDAO)) {
			return null;
		}

		final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureForLastBRInterviewRound = jobRoleToInterviewStructureDAOsForBRRounds
				.get(jobRoleToInterviewStructureDAOsForBRRounds.size() - 1);

		return this.getRecommendation(
				evaluationDAO,
				jobRoleToInterviewStructureForLastBRInterviewRound);
	}

	private boolean areAllBRRoundsCompletedForJobRoleExceptForCurrentInterview(
			final EvaluationDAO evaluationDAO,
			final List<JobRoleToInterviewStructureDAO> jobRoleToInterviewStructureDAOsWithBarRaiserInterviewRound,
			final InterviewDAO currentInterview) {
		final List<InterviewDAO> interviews = this.interViewRepository.findAllByEvaluationId(evaluationDAO.getId());
		int doneInterviewCount = 0;
		for (InterviewDAO interview : interviews) {
			if (InterviewStatus.DONE.getValue().equals(interview.getStatus())
					&& !interview.getInterviewRound().equals(Constants.ROUND_TYPE_INTERNAL)) {
				doneInterviewCount++;
			}
			if (currentInterview.getId().equals(interview.getId())
					&& this.shouldCurrentInterviewBeConsideredForRecommendationGeneration(interview)) {
				doneInterviewCount++;
			}
		}

		return doneInterviewCount == jobRoleToInterviewStructureDAOsWithBarRaiserInterviewRound.size();
	}

	private List<JobRoleToInterviewStructureDAO> getJobRoleToInterviewStructureForBarRaiserInterviewRound(
			final EvaluationDAO evaluationDAO) {
		return this.jobRoleToInterviewStructureRepository
				.findAllByJobRoleIdAndJobRoleVersion(
						evaluationDAO.getJobRoleId(),
						evaluationDAO.getJobRoleVersion())
				.stream()
				.filter(jobRoleToInterviewStructureDAO -> !jobRoleToInterviewStructureDAO
						.getInterviewRound()
						.equals(Constants.ROUND_TYPE_INTERNAL))
				.sorted(Comparator.comparing(JobRoleToInterviewStructureDAO::getOrderIndex))
				.collect(Collectors.toList());
	}

	public EvaluationRecommendation getRecommendation(
			final EvaluationDAO evaluationDAO,
			final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO) {
		final Double cumulativeCutOff = this.getCumulativeCutOffScore(jobRoleToInterviewStructureDAO);
		final Integer bgsScore = this.bgsScoreFetcher
				.getBgsScoreForEvaluationBasedOnInterviewProcessType(
						evaluationDAO.getId(),
						InterviewProcessType.BARRAISER);
		// Tp-510 : we should include skill level cutoffs here too
		if (this.isCandidateRejected(cumulativeCutOff,
				bgsScore)) {
			return EvaluationRecommendation.builder().recommendationType(RecommendationType.NOT_RECOMMENDED)
					.screeningCutOff(cumulativeCutOff.intValue()).build();
		}
		if (bgsScore > this.getCutOffForStronglyRecommended(cumulativeCutOff).intValue()) {
			return EvaluationRecommendation.builder().recommendationType(RecommendationType.STRONGLY_RECOMMENDED)
					.screeningCutOff(cumulativeCutOff.intValue()).build();
		} else if (bgsScore > this.getCutOffForRecommended(cumulativeCutOff).intValue()) {
			return EvaluationRecommendation.builder().recommendationType(RecommendationType.RECOMMENDED)
					.screeningCutOff(cumulativeCutOff.intValue()).build();
		} else
			return EvaluationRecommendation.builder().recommendationType(RecommendationType.REQUIRES_FURTHER_REVIEW)
					.screeningCutOff(cumulativeCutOff.intValue()).build();
	}

	private boolean areAllBRRoundsCompletedForJobRole(
			final EvaluationDAO evaluationDAO,
			final List<JobRoleToInterviewStructureDAO> jobRoleToInterviewStructureDAOsWithBarRaiserInterviewRound) {
		final List<InterviewDAO> interviews = this.interViewRepository.findAllByEvaluationId(evaluationDAO.getId());
		int doneInterviewCount = 0;
		for (InterviewDAO interview : interviews) {
			if (InterviewStatus.DONE.getValue().equals(interview.getStatus())
					&& !interview.getInterviewRound().equals(Constants.ROUND_TYPE_INTERNAL)) {
				doneInterviewCount++;
			}
		}

		return doneInterviewCount == jobRoleToInterviewStructureDAOsWithBarRaiserInterviewRound.size();
	}

	private Double getCutOffForRecommended(final Double cumulativeCutOff) {
		return (MAX_BGS_SCORE - cumulativeCutOff)
				* (CONSTANT_FOR_RECOMMENDED - (Math.floor(cumulativeCutOff / 50) - 7) / 50) + cumulativeCutOff;
	}

	private Double getCutOffForStronglyRecommended(final Double cumulativeCutOff) {
		return (MAX_BGS_SCORE - cumulativeCutOff)
				* (CONSTANT_FOR_STRONGLY_RECOMMENDED - (Math.floor(cumulativeCutOff / 50) - 7) / 50) + cumulativeCutOff;
	}

	private boolean isCandidateRejected(final Double rejectionCutoffScore,
			final Integer bgsScore) {
		return bgsScore < rejectionCutoffScore;
	}

	// change default cutoff store
	private Double getCumulativeCutOffScore(final JobRoleToInterviewStructureDAO jobRoleToInterviewStructureDAO) {
		return jobRoleToInterviewStructureDAO.getRecommendationScore() != null
				? jobRoleToInterviewStructureDAO.getRecommendationScore().doubleValue()
				: Double.parseDouble(this.appConfigProperties.getString(DEFAULT_CUTOFF_SCORE));
	}

	private Boolean isRecommendationAlreadyCalculated(final String evaluationId) {
		return this.evaluationRecommendationRepository
				.findByEvaluationIdAndRecommendationAlgoVersion(evaluationId, RECOMMENDATION_ALGO_VERSION).isPresent();
	}

	private Boolean shouldCurrentInterviewBeConsideredForRecommendationGeneration(final InterviewDAO interview) {
		return !InterviewStatus.DONE.getValue().equals(interview.getStatus()) &&
				!interview.getInterviewRound().equals(Constants.ROUND_TYPE_INTERNAL);
	}
}