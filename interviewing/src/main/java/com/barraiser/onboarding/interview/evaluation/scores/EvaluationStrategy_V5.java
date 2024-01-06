/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores;

import com.barraiser.common.graphql.types.SkillScore;
import com.barraiser.onboarding.common.Constants;
import com.barraiser.onboarding.interview.pojo.FeedbackData;
import com.barraiser.onboarding.interview.pojo.QuestionData;
import com.barraiser.onboarding.interview.evaluation.scores.pojo.ComputeEvaluationScoresData;
import com.barraiser.onboarding.interview.evaluation.scores.pojo.EvaluationScoreData;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
public class EvaluationStrategy_V5 implements EvaluationStrategy {

	public static final String SCORING_ALGO_VERSION = "5";
	final ObjectMapper objectMapper;

	@Override
	public String version() {
		return SCORING_ALGO_VERSION;
	}

	@Override
	public EvaluationScoreData computeEvaluationScore(final ComputeEvaluationScoresData input) {

		final Map<String, Double> skillWeightageMap = input.getSkillWeightageMap();
		// Get aggregated score per category
		final Map<String, Double> scorePerCategory = this.getAggregateScorePerCategory(input.getQuestions());
		final Map<String, Double> finalWeightage = this.getApplicableWeightage(skillWeightageMap,
				scorePerCategory.keySet());

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

	// Sometimes, there could be a category which is not present in the feedbacks,
	// we need to split
	// that into other
	// categories
	private Map<String, Double> getApplicableWeightage(
			final Map<String, Double> defaultWeightage, final Set<String> categoriesInFeedback) {
		final List<String> missingCategories = new ArrayList<>(defaultWeightage.keySet());
		missingCategories.removeAll(categoriesInFeedback);

		final Double weightageToDistribute = missingCategories.stream()
				.map(defaultWeightage::get)
				.mapToDouble(Double::doubleValue)
				.sum();

		final Double totalWeightageAcrossCategoriesPresentInFeedback = categoriesInFeedback.stream()
				.map(defaultWeightage::get)
				.mapToDouble(Double::doubleValue)
				.sum();

		final Map<String, Double> finalWeightage = new HashMap<>();
		categoriesInFeedback.forEach(
				x -> {

					// Distributing the remaining weightage in the ratio of current weightages to
					// categories.
					final Double weightageToAdd = weightageToDistribute
							* (defaultWeightage.get(x)
									/ totalWeightageAcrossCategoriesPresentInFeedback);
					double w = defaultWeightage.get(x) + weightageToAdd;
					w = w * 100;
					w = (int) w / 100D;
					finalWeightage.put(x, w);
				});

		return finalWeightage;
	}

	private Map<String, Double> getAggregateScorePerCategory(final List<QuestionData> questions) {
		final Map<String, Double> modifiedWeightageMapOfDifficulty = EvaluationStrategy_V5Constants.ModifiedWeightage.difficultyMap;
		final Map<String, Double> modifiedWeightageMapOfQuestionType = EvaluationStrategy_V5Constants.ModifiedWeightage.questionTypeMap;
		final Map<String, List<Double>> categoryLevelScores = new HashMap<>();
		final Map<String, List<Double>> categoryLevelModifiedWeightages = new HashMap<>();

		for (final QuestionData question : questions) {
			final boolean isQuestionDeletedOrNonEvaluative = EvaluationStrategy_V5Constants.QuestionType.DELETED
					.getValue()
					.equals(question.getType())
					|| EvaluationStrategy_V5Constants.QuestionType.NON_EVALUATIVE
							.getValue()
							.equals(question.getType());

			if (!isQuestionDeletedOrNonEvaluative) {
				for (final FeedbackData feedback : question.getFeedbacks()) {
					if (feedback.getRating() != 0) {
						final List<Double> categoryScores = categoryLevelScores.computeIfAbsent(
								feedback.getCategoryId(), x -> new ArrayList<>());
						final List<Double> categoryModifiedWeightage = categoryLevelModifiedWeightages.computeIfAbsent(
								feedback.getCategoryId(), x -> new ArrayList<>());

						final int baseScore;
						final int scoreFactor;

						final EvaluationStrategy_V5Constants.FeedbackDifficulty difficulty = EvaluationStrategy_V5Constants.FeedbackDifficulty
								.fromString(
										Constants.SOFT_SKILL_ID.equals(feedback.getCategoryId())
												? EvaluationStrategy_V5Constants.FeedbackDifficulty.HARD
														.getValue()
												: feedback.getDifficulty());

						switch (difficulty) {
							case HARD:
								baseScore = 400;
								scoreFactor = 40;
								break;

							case MODERATE:
								baseScore = 250;
								scoreFactor = 55;
								break;

							case EASY:
								baseScore = 100;
								scoreFactor = 55;
								break;

							default:
								throw new IllegalArgumentException(
										"Invalid Difficulty : " + difficulty.getValue());
						}
						final Double modifiedWeightage = modifiedWeightageMapOfDifficulty.get(
								difficulty.getValue() + "_" + feedback.getRating())
								* modifiedWeightageMapOfQuestionType.get(
										question.getType() + "_" + feedback.getRating());
						final double feedbackLevelScore = (baseScore + feedback.getRating() * scoreFactor)
								* modifiedWeightage;
						categoryScores.add(feedbackLevelScore);
						categoryModifiedWeightage.add(modifiedWeightage);
					}
				}
			}
		}

		final Map<String, Double> aggregatedScore = new HashMap<>();
		for (final String categoryId : categoryLevelScores.keySet()) {
			final Double categoryScoreSum = categoryLevelScores.get(categoryId).stream().mapToDouble(x -> x).sum();
			final Double totalModifiedWeightageForCategory = categoryLevelModifiedWeightages.get(categoryId).stream()
					.mapToDouble(x -> x)
					.sum();

			aggregatedScore.put(categoryId, categoryScoreSum / totalModifiedWeightageForCategory);
		}
		return aggregatedScore;
	}
}
