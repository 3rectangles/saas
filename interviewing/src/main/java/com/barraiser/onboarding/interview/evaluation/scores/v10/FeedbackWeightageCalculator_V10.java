/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores.v10;

import java.util.*;

public class FeedbackWeightageCalculator_V10 {
	public static final Double DEFAULT_FEEDBACK_WEIGHTAGE = 1D;
	public static final String FEEDBACK_DIFFICULTY = "feedback_difficulty";
	public static final String RATING_LOWER_BOUND = "rating_lower_bound";
	public static final String RATING_UPPER_BOUND = "rating_upper_bound";
	public static final String MODIFIED_WEIGHTAGE = "modified_weightage";

	private static final List<Map<String, Object>> difficultyToWeightageMapping = List.of(
			Map.of(
					FEEDBACK_DIFFICULTY, EvaluationStrategy_V10Constants.FeedbackDifficulty.HARD,
					RATING_LOWER_BOUND, 8D,
					RATING_UPPER_BOUND, 10D,
					MODIFIED_WEIGHTAGE, 2D),
			Map.of(
					FEEDBACK_DIFFICULTY, EvaluationStrategy_V10Constants.FeedbackDifficulty.HARD,
					RATING_LOWER_BOUND, 6D,
					RATING_UPPER_BOUND, 8D,
					MODIFIED_WEIGHTAGE, 1.5D),
			Map.of(
					FEEDBACK_DIFFICULTY, EvaluationStrategy_V10Constants.FeedbackDifficulty.HARD,
					RATING_LOWER_BOUND, 5.999D,
					RATING_UPPER_BOUND, 6D,
					MODIFIED_WEIGHTAGE, 1.5D),
			Map.of(
					FEEDBACK_DIFFICULTY, EvaluationStrategy_V10Constants.FeedbackDifficulty.MODERATE,
					RATING_LOWER_BOUND, 0D,
					RATING_UPPER_BOUND, 1D,
					MODIFIED_WEIGHTAGE, 1.5D),
			Map.of(
					FEEDBACK_DIFFICULTY, EvaluationStrategy_V10Constants.FeedbackDifficulty.MODERATE,
					RATING_LOWER_BOUND, 1D,
					RATING_UPPER_BOUND, 2D,
					MODIFIED_WEIGHTAGE, 1.4D),
			Map.of(
					FEEDBACK_DIFFICULTY, EvaluationStrategy_V10Constants.FeedbackDifficulty.MODERATE,
					RATING_LOWER_BOUND, 2D,
					RATING_UPPER_BOUND, 3D,
					MODIFIED_WEIGHTAGE, 1.3D),
			Map.of(
					FEEDBACK_DIFFICULTY, EvaluationStrategy_V10Constants.FeedbackDifficulty.MODERATE,
					RATING_LOWER_BOUND, 3D,
					RATING_UPPER_BOUND, 4D,
					MODIFIED_WEIGHTAGE, 1.2D),
			Map.of(
					FEEDBACK_DIFFICULTY, EvaluationStrategy_V10Constants.FeedbackDifficulty.MODERATE,
					RATING_LOWER_BOUND, 4D,
					RATING_UPPER_BOUND, 5D,
					MODIFIED_WEIGHTAGE, 1.1D),
			Map.of(
					FEEDBACK_DIFFICULTY, EvaluationStrategy_V10Constants.FeedbackDifficulty.MODERATE,
					RATING_LOWER_BOUND, 5D,
					RATING_UPPER_BOUND, 6D,
					MODIFIED_WEIGHTAGE, 1D),
			Map.of(
					FEEDBACK_DIFFICULTY, EvaluationStrategy_V10Constants.FeedbackDifficulty.MODERATE,
					RATING_LOWER_BOUND, 6D,
					RATING_UPPER_BOUND, 7D,
					MODIFIED_WEIGHTAGE, 1.25D),
			Map.of(
					FEEDBACK_DIFFICULTY, EvaluationStrategy_V10Constants.FeedbackDifficulty.MODERATE,
					RATING_LOWER_BOUND, 7D,
					RATING_UPPER_BOUND, 8D,
					MODIFIED_WEIGHTAGE, 1.5D),
			Map.of(
					FEEDBACK_DIFFICULTY, EvaluationStrategy_V10Constants.FeedbackDifficulty.MODERATE,
					RATING_LOWER_BOUND, 8D,
					RATING_UPPER_BOUND, 9D,
					MODIFIED_WEIGHTAGE, 1.75D),
			Map.of(
					FEEDBACK_DIFFICULTY, EvaluationStrategy_V10Constants.FeedbackDifficulty.MODERATE,
					RATING_LOWER_BOUND, 9D,
					RATING_UPPER_BOUND, 10D,
					MODIFIED_WEIGHTAGE, 2D),
			Map.of(
					FEEDBACK_DIFFICULTY, EvaluationStrategy_V10Constants.FeedbackDifficulty.EASY,
					RATING_LOWER_BOUND, 9D,
					RATING_UPPER_BOUND, 10D,
					MODIFIED_WEIGHTAGE, 1.5D),
			Map.of(
					FEEDBACK_DIFFICULTY, EvaluationStrategy_V10Constants.FeedbackDifficulty.EASY,
					RATING_LOWER_BOUND, 8.999D,
					RATING_UPPER_BOUND, 9D,
					MODIFIED_WEIGHTAGE, 1.5D),
			Map.of(
					FEEDBACK_DIFFICULTY, EvaluationStrategy_V10Constants.FeedbackDifficulty.EASY,
					RATING_LOWER_BOUND, 0D,
					RATING_UPPER_BOUND, 5D,
					MODIFIED_WEIGHTAGE, 1.5D));

	public static Double getModifiedWeightage(final EvaluationStrategy_V10Constants.FeedbackDifficulty difficulty,
			final Float rating) {
		final double roundedRating = Math.floor(rating * 1000) / 1000D;
		for (Map<String, Object> obj : difficultyToWeightageMapping) {
			if (difficulty.equals(obj.get(FEEDBACK_DIFFICULTY)) && roundedRating > (Double) obj.get(RATING_LOWER_BOUND)
					&&
					rating <= (Double) obj.get(RATING_UPPER_BOUND)) {
				return (Double) obj.get(MODIFIED_WEIGHTAGE);
			}
		}
		return DEFAULT_FEEDBACK_WEIGHTAGE;
	}
}
