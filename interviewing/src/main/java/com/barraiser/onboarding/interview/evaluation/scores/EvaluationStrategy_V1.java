/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores;

import com.barraiser.common.graphql.types.SkillScore;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.pojo.FeedbackData;
import com.barraiser.onboarding.interview.pojo.QuestionData;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.evaluation.scores.pojo.ComputeEvaluationScoresData;
import com.barraiser.onboarding.interview.evaluation.scores.pojo.EvaluationScoreData;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@AllArgsConstructor
public class EvaluationStrategy_V1 implements EvaluationStrategy {

	public static final String SCORING_ALGO_VERSION = "1";
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

		log.info("Evaluation strategy 1");

		final Map<String, Double> skillWeightageMap = input.getSkillWeightageMap();
		// Get aggregated score per category
		final Map<String, Double> scorePerCategory = this.getAggregateScorePerCategory(input.getQuestions());
		final Map<String, Double> finalWeightage = this.getApplicableWeightage(skillWeightageMap,
				scorePerCategory.keySet());

		final List<SkillScore> scores = scorePerCategory.keySet().stream()
				.map(
						x -> this.evaluationScoreRepository
								.findByEvaluationIdAndSkillIdAndScoringAlgoVersion(
										input.getEvaluationId(),
										x,
										SCORING_ALGO_VERSION)
								.orElse(
										EvaluationScoreDAO.builder()
												.id(UUID.randomUUID().toString())
												.evaluationId(
														input.getEvaluationId())
												.skillId(x)
												.scoringAlgoVersion(
														SCORING_ALGO_VERSION)
												.build())
								.toBuilder()
								.score((double) scorePerCategory.get(x).intValue())
								.weightage(finalWeightage.get(x))
								.build())
				.map(x -> this.objectMapper.convertValue(x, SkillScore.class))
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
		final Double weightageToAdd = weightageToDistribute / categoriesInFeedback.size();
		final Map<String, Double> finalWeightage = new HashMap<>();
		categoriesInFeedback.forEach(
				x -> {
					double w = defaultWeightage.get(x) + weightageToAdd;
					w = w * 100;
					w = (int) w / 100D;
					finalWeightage.put(x, w);
				});

		return finalWeightage;
	}

	private Map<String, Double> getAggregateScorePerCategory(final List<QuestionData> questions) {

		final Map<String, List<Float>> categoryLevelScores = new HashMap<>();
		final Map<String, List<Float>> categoryLevelWeights = new HashMap<>();
		for (final QuestionData question : questions) {
			for (final FeedbackData feedback : question.getFeedbacks()) {
				final List<Float> categoryScores = categoryLevelScores.computeIfAbsent(
						feedback.getCategoryId(), x -> new ArrayList<>());
				final List<Float> categoryWeight = categoryLevelWeights.computeIfAbsent(
						feedback.getCategoryId(), x -> new ArrayList<>());
				final int baseScore = question.getDifficulty().equals("HARD")
						? 400
						: question.getDifficulty().equals("MEDIUM") ? 200 : 100;
				final int scoreFactor = question.getDifficulty().equals("HARD")
						? 40
						: question.getDifficulty().equals("MEDIUM") ? 60 : 70;
				final float feedbackLevelScore = (baseScore + feedback.getRating() * scoreFactor)
						* feedback.getWeightage();
				categoryScores.add(feedbackLevelScore);
				categoryWeight.add(feedback.getWeightage());
			}
		}

		final Map<String, Double> aggregatedScore = new HashMap<>();
		for (final String categoryId : categoryLevelScores.keySet()) {
			final Double categoryScoreSum = categoryLevelScores.get(categoryId).stream()
					.mapToDouble(Float::doubleValue)
					.sum();
			final Double categoryWeightSum = categoryLevelWeights.get(categoryId).stream()
					.mapToDouble(Float::doubleValue)
					.sum();
			aggregatedScore.put(categoryId, categoryScoreSum / categoryWeightSum);
		}
		return aggregatedScore;
	}
}
