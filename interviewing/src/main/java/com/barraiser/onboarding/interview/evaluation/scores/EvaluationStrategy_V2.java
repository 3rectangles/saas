/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores;

import com.barraiser.common.graphql.types.SkillScore;
import com.barraiser.onboarding.dal.EvaluationRepository;
import com.barraiser.onboarding.dal.EvaluationScoreRepository;
import com.barraiser.onboarding.dal.QuestionRepository;
import com.barraiser.onboarding.dal.SkillWeightageRepository;
import com.barraiser.onboarding.interview.pojo.FeedbackData;
import com.barraiser.onboarding.interview.pojo.QuestionData;
import com.barraiser.onboarding.interview.InterViewRepository;
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
public class EvaluationStrategy_V2 implements EvaluationStrategy {
	public static final String SCORING_ALGO_VERSION = "2";
	final QuestionRepository questionRepository;
	final InterViewRepository interViewRepository;
	final SkillWeightageRepository skillWeightageRepository;
	final EvaluationRepository evaluationRepository;
	final EvaluationScoreRepository evaluationScoreRepository;
	final ObjectMapper objectMapper;

	@Override
	public String version() {
		return SCORING_ALGO_VERSION;
	}

	@Override
	public EvaluationScoreData computeEvaluationScore(final ComputeEvaluationScoresData input) {

		log.info("Evaluation strategy 2");

		final Map<String, Double> skillWeightageMap = input.getSkillWeightageMap();
		// Get aggregated score per category
		final Map<String, Double> scorePerCategory = this.getAggregateScorePerCategory(input.getQuestions());
		final Map<String, Double> finalWeightage = this.getApplicableWeightage(skillWeightageMap,
				scorePerCategory.keySet());

		/*
		 * final Double totalScore = scorePerCategory.keySet().stream()
		 * .map(x -> scorePerCategory.get(x) * finalWeightage.get(x))
		 * .mapToDouble(Double::doubleValue)
		 * .sum();
		 */

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

					// Distributing the remaining weigtage in the ratio of current weightages to
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
		final Map<String, List<Float>> categoryLevelScores = new HashMap<>();
		for (final QuestionData question : questions) {
			final boolean isQuestionIrrelevant = question.getIrrelevant() != null ? question.getIrrelevant() : false;

			if (!isQuestionIrrelevant) {
				for (final FeedbackData feedback : question.getFeedbacks()) {
					if (feedback.getRating() != 0) {
						final List<Float> categoryScores = categoryLevelScores.computeIfAbsent(
								feedback.getCategoryId(), x -> new ArrayList<>());
						final int baseScore;
						final int scoreFactor;
						switch (feedback.getDifficulty()) {
							case "VERY_HARD":
								baseScore = 400;
								scoreFactor = 40;
								break;
							case "HARD":
								baseScore = 300;
								scoreFactor = 50;
								break;

							case "MODERATE":
								baseScore = 200;
								scoreFactor = 60;
								break;
							case "EASY":
								baseScore = 100;
								scoreFactor = 70;
								break;

							default:
								baseScore = 0;
								scoreFactor = 80;
								break;
						}
						final float feedbackLevelScore = (baseScore + feedback.getRating() * scoreFactor);
						categoryScores.add(feedbackLevelScore);
					}
				}
			}
		}

		final Map<String, Double> aggregatedScore = new HashMap<>();
		for (final String categoryId : categoryLevelScores.keySet()) {
			final Double categoryScoreSum = categoryLevelScores.get(categoryId).stream()
					.mapToDouble(Float::doubleValue)
					.sum();
			final Integer numberOfFeedbacksAtCategoryLevel = categoryLevelScores.get(categoryId).size();
			aggregatedScore.put(categoryId, categoryScoreSum / numberOfFeedbacksAtCategoryLevel);
		}
		return aggregatedScore;
	}
}
