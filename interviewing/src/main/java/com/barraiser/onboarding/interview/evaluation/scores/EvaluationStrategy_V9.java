/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores;

import com.barraiser.common.graphql.types.SkillScore;
import com.barraiser.onboarding.interview.pojo.FeedbackData;
import com.barraiser.onboarding.interview.pojo.QuestionData;
import com.barraiser.onboarding.interview.evaluation.EvaluationScoreComputationManager;
import com.barraiser.onboarding.interview.evaluation.scores.pojo.ComputeEvaluationScoresData;

import com.barraiser.onboarding.interview.evaluation.scores.pojo.EvaluationScoreData;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
public class EvaluationStrategy_V9 implements EvaluationStrategy {
	public static final String SCORING_ALGO_VERSION = "9";

	private final EvaluationScoreComputationManager evaluationScoreComputationManager;

	@Override
	public String version() {
		return SCORING_ALGO_VERSION;
	}

	@Override
	public EvaluationScoreData computeEvaluationScore(final ComputeEvaluationScoresData input) {

		final Map<String, Double> skillWeightageMap = input.getSkillWeightageMap();
		// Get aggregated score per category
		final Map<String, Double> scorePerCategory = this.getAggregateScorePerCategory(
				input.getQuestions(), input.getSoftSkillFeedbackList().get(0));
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

		return EvaluationScoreData.builder().skillScores(scores).build();
	}

	private Map<String, Double> getAggregateScorePerCategory(
			final List<QuestionData> questions, final FeedbackData softSkillsFeedback) {
		final Map<String, Double> sumOfFeedbackScorePerCategory = new HashMap<>();
		final Map<String, Double> sumOfFeedbackWeightsPerCategory = new HashMap<>();
		for (final QuestionData question : questions) {
			if (this.isQuestionToBeConsidered(question)) {
				for (final FeedbackData feedback : question.getFeedbacks()) {
					this.updateFeedbackScoreAndWeightsPerCategory(
							feedback,
							question.getType(),
							sumOfFeedbackScorePerCategory,
							sumOfFeedbackWeightsPerCategory);
				}
			}
		}

		this.updateFeedbackScoreAndWeightsPerCategory(
				softSkillsFeedback,
				EvaluationStrategy_V9Constants.QuestionType.REQUIRED.getValue(),
				sumOfFeedbackScorePerCategory,
				sumOfFeedbackWeightsPerCategory);

		final Map<String, Double> weightedAverageCategoryLevel = new HashMap<>();
		sumOfFeedbackScorePerCategory.forEach(
				(x, y) -> {
					weightedAverageCategoryLevel.put(x, y / sumOfFeedbackWeightsPerCategory.get(x));
				});
		return weightedAverageCategoryLevel;
	}

	private Float getFeedbackScore(final FeedbackData feedback) {
		final Float normalisedRating = feedback.getNormalisedRating();
		final EvaluationStrategy_V9Constants.FeedbackDifficulty difficulty = EvaluationStrategy_V9Constants.FeedbackDifficulty
				.fromString(
						feedback.getDifficulty());
		final Integer scoreFactor;
		switch (difficulty) {
			case EASY:
				scoreFactor = normalisedRating > 5 ? 40 : 75;
				break;
			case MODERATE:
				scoreFactor = 60;
				break;
			case HARD:
				scoreFactor = normalisedRating > 5 ? 70 : 50;
				break;
			default:
				throw new IllegalArgumentException("Invalid Difficulty : " + difficulty.getValue());
		}
		return 450 + (normalisedRating - 5) * scoreFactor;
	}

	private Double getFeedbackWeightage(final FeedbackData feedback, final String type) {
		final Double modifiedWeightageOfQuestion = QuestionTypeWeightageCalculator_V9.getModifiedWeightage(
				type, feedback.getNormalisedRating());
		final Double modifiedWeightageOfFeedback = FeedbackWeightageCalculator_V9.getModifiedWeightage(
				feedback.getDifficulty(), feedback.getNormalisedRating());
		return modifiedWeightageOfQuestion * modifiedWeightageOfFeedback;
	}

	private boolean isQuestionToBeConsidered(final QuestionData question) {
		return !(EvaluationStrategy_V9Constants.QuestionType.DELETED
				.getValue()
				.equals(question.getType())
				|| EvaluationStrategy_V9Constants.QuestionType.NON_EVALUATIVE
						.getValue()
						.equals(question.getType()));
	}

	private void updateFeedbackScoreAndWeightsPerCategory(
			final FeedbackData feedbackData,
			final String questionType,
			final Map<String, Double> sumOfFeedbackScorePerCategory,
			final Map<String, Double> sumOfFeedbackWeightsPerCategory) {
		if (feedbackData.getRating() != 0) {
			Double categoryScore = sumOfFeedbackScorePerCategory.getOrDefault(feedbackData.getCategoryId(), 0D);
			final Double categoryWeights = sumOfFeedbackWeightsPerCategory.getOrDefault(feedbackData.getCategoryId(),
					0D);
			final Double feedbackWeightage = this.getFeedbackWeightage(feedbackData, questionType);
			categoryScore += this.getFeedbackScore(feedbackData) * feedbackWeightage;
			sumOfFeedbackScorePerCategory.put(feedbackData.getCategoryId(), categoryScore);
			sumOfFeedbackWeightsPerCategory.put(
					feedbackData.getCategoryId(), categoryWeights + feedbackWeightage);
		}
	}
}
