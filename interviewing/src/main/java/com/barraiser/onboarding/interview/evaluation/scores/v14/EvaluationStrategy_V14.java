/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores.v14;

import com.barraiser.common.graphql.types.QuestionType;
import com.barraiser.common.graphql.types.SkillScore;
import com.barraiser.onboarding.config.ConfigComposer;
import com.barraiser.onboarding.dal.EvaluationDAO;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.dal.PartnerCompanyDAO;
import com.barraiser.onboarding.dal.PartnerCompanyRepository;
import com.barraiser.onboarding.interview.evaluation.EvaluationScoreComputationManager;
import com.barraiser.onboarding.interview.evaluation.scores.EvaluationStrategy;
import com.barraiser.onboarding.interview.evaluation.scores.NormalisationVersionFetcher;
import com.barraiser.onboarding.interview.evaluation.scores.pojo.ComputeEvaluationScoresData;
import com.barraiser.onboarding.interview.evaluation.scores.pojo.EvaluationScoreData;
import com.barraiser.onboarding.interview.feeback.FeedbackNormalisationUtil;
import com.barraiser.onboarding.interview.pojo.FeedbackData;
import com.barraiser.onboarding.interview.pojo.InterviewData;
import com.barraiser.onboarding.interview.pojo.QuestionData;
import com.fasterxml.jackson.databind.JsonNode;
import com.barraiser.common.enums.Weightage;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class EvaluationStrategy_V14 implements EvaluationStrategy {
	public static final String SCORING_ALGO_VERSION = "14";

	private final EvaluationScoreComputationManager evaluationScoreComputationManager;
	private final FeedbackNormalisationUtil feedbackNormalisationUtil;
	private final ConfigComposer configComposer;
	private final EvaluationRepository evaluationRepository;
	private final PartnerCompanyRepository partnerCompanyRepository;

	@Override
	public EvaluationScoreData computeEvaluationScore(ComputeEvaluationScoresData input) {

		final EvaluationDAO evaluationDAO = evaluationRepository.findById(input.getEvaluationId()).get();
		final PartnerCompanyDAO partnerCompanyDAO = partnerCompanyRepository.findById(evaluationDAO.getPartnerId())
				.get();
		final Map<String, Double> skillWeightageMap = input.getSkillWeightageMap();
		// Get aggregated score per category
		final Map<String, Double> scorePerCategory = this.getAggregateScorePerCategory(input.getInterviews(),
				input.getSoftSkillFeedbackList(), partnerCompanyDAO);
		final Map<String, Double> finalWeightage = this.evaluationScoreComputationManager.getApplicableWeightage(
				skillWeightageMap, scorePerCategory.keySet());

		final List<SkillScore> scores = scorePerCategory.keySet().stream()
				.map(
						x -> SkillScore.builder()
								.skillId(x)
								.score((double) scorePerCategory.get(x).intValue())
								.weightage(finalWeightage.get(x))
								.build())
				.collect(Collectors.toList());

		return EvaluationScoreData.builder()
				.skillScores(scores)
				.input(input)
				.build();
	}

	private Map<String, Double> getAggregateScorePerCategory(final List<InterviewData> interviews,
			final List<FeedbackData> softSkillsFeedback, final PartnerCompanyDAO partnerCompanyDAO) {
		final Map<String, Double> sumOfFeedbackScorePerCategory = new HashMap<>();
		final Map<String, Double> sumOfFeedbackWeightsPerCategory = new HashMap<>();
		final List<QuestionData> questions = interviews.stream()
				.map(InterviewData::getQuestions)
				.flatMap(Collection::stream).collect(Collectors.toList());
		for (final QuestionData question : questions) {
			if (this.isQuestionToBeConsidered(question)) {
				for (final FeedbackData feedback : question.getFeedbacks()) {
					this.updateFeedbackScoreAndWeightsPerCategory(
							feedback,
							sumOfFeedbackScorePerCategory,
							sumOfFeedbackWeightsPerCategory,
							partnerCompanyDAO);
				}
			}
		}

		for (final FeedbackData feedbackData : softSkillsFeedback) {
			this.updateFeedbackScoreAndWeightsPerCategory(
					feedbackData,
					sumOfFeedbackScorePerCategory,
					sumOfFeedbackWeightsPerCategory,
					partnerCompanyDAO);
		}

		final Map<String, Double> weightedAverageCategoryLevel = new HashMap<>();
		sumOfFeedbackScorePerCategory.forEach(
				(x, y) -> {
					weightedAverageCategoryLevel.put(x, y / sumOfFeedbackWeightsPerCategory.get(x));
				});
		return weightedAverageCategoryLevel;
	}

	private void updateFeedbackScoreAndWeightsPerCategory(
			final FeedbackData feedbackData,
			final Map<String, Double> sumOfFeedbackScorePerCategory,
			final Map<String, Double> sumOfFeedbackWeightsPerCategory,
			final PartnerCompanyDAO partnerCompanyDAO) {
		if (feedbackData.getRating() != 0) {
			Double categoryScore = sumOfFeedbackScorePerCategory.getOrDefault(feedbackData.getCategoryId(), 0D);
			final Double categoryWeights = sumOfFeedbackWeightsPerCategory.getOrDefault(feedbackData.getCategoryId(),
					0D);
			final Double feedbackWeightage = this.getFeedbackWeightage(feedbackData.getFeedbackWeightage());
			categoryScore += this.getFeedbackScore(feedbackData, partnerCompanyDAO) * feedbackWeightage;
			sumOfFeedbackScorePerCategory.put(feedbackData.getCategoryId(), categoryScore);
			sumOfFeedbackWeightsPerCategory.put(
					feedbackData.getCategoryId(), categoryWeights + feedbackWeightage);
		}
	}

	private boolean isQuestionToBeConsidered(final QuestionData question) {
		return !(QuestionType.DELETED
				.getValue()
				.equals(question.getType())
				|| QuestionType.NON_EVALUATIVE
						.getValue()
						.equals(question.getType()));
	}

	private Float getFeedbackScore(final FeedbackData feedback, final PartnerCompanyDAO partnerCompanyDAO) {
		// using config to set if nomalizasion and lenearity condition depending on
		// interview type
		Boolean normalisationFlag = false;
		Boolean linerFlag = true;

		List<String> tags = new ArrayList<>();
		tags.add(feedback.getIsSaasFeedback() ? "interview_type.internal" : "interview_type.br");

		try {
			JsonNode config = configComposer.compose("scoring_rating", tags);
			JsonNode configScoringBGS = config != null ? config.get("scoring_bgs") : null;
			normalisationFlag = configScoringBGS != null && configScoringBGS.get("normalizaton").asBoolean();
			linerFlag = configScoringBGS != null && configScoringBGS.get("linear").asBoolean();
		} catch (Exception e) {
			normalisationFlag = false;
			linerFlag = true;
		}

		if (partnerCompanyDAO.getIsNormalisationEnabled() != null) {
			normalisationFlag = partnerCompanyDAO.getIsNormalisationEnabled();
		}
		Float normalisedRating = normalisationFlag ? this.feedbackNormalisationUtil.getCappedNormalisedRating(feedback,
				NormalisationVersionFetcher.getNormalisationAlgoVersion(SCORING_ALGO_VERSION))
				: feedback.getRating();
		if (partnerCompanyDAO.getIsLinearScore() != null) {
			linerFlag = partnerCompanyDAO.getIsLinearScore();
		}
		if (!linerFlag) {
			normalisedRating = 510 + (normalisedRating - 6) * (Math.abs(normalisedRating - 6) * 10 + 32.5F);
		} else {
			normalisedRating = (normalisedRating / 10) * 800;
		}
		return normalisedRating;
	}

	private Double getFeedbackWeightage(final Weightage weightage) {
		if (Weightage.EASY.equals(weightage)) {
			return 1D;
		} else if (Weightage.MODERATE.equals(weightage)) {
			return 2D;
		} else if (Weightage.HARD.equals(weightage)) {
			return 3D;
		}
		throw new IllegalArgumentException("No mapping available for feedback weightage " + weightage);
	}

	@Override
	public String version() {
		return SCORING_ALGO_VERSION;
	}
}
