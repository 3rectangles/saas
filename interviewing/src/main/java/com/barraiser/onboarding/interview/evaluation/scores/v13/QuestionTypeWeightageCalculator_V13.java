/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores.v13;

import java.util.List;
import java.util.Map;

public class QuestionTypeWeightageCalculator_V13 {
	public static final String QUESTION_TYPE = "question_type";
	public static final String RATING_UPPER_BOUND = "rating_upper_bound";
	public static final String RATING_LOWER_BOUND = "rating_lower_bound";
	public static final String MODIFIED_WEIGHTAGE = "modified_weightage";
	public static final Double DEFAULT_MODIFIED_WEIGHTAGE = 0D;
	private static final List<Map<String, Object>> questionTypeToModifiedWeightageMapping = List.of(
			Map.of(
					QUESTION_TYPE, EvaluationStrategy_V13Constants.QuestionType.REQUIRED,
					RATING_LOWER_BOUND, 0D,
					RATING_UPPER_BOUND, 10D,
					MODIFIED_WEIGHTAGE, 1D),
			Map.of(
					QUESTION_TYPE, EvaluationStrategy_V13Constants.QuestionType.GOOD_TO_KNOW,
					RATING_LOWER_BOUND, 9D,
					RATING_UPPER_BOUND, 10D,
					MODIFIED_WEIGHTAGE, 1.5D),
			Map.of(
					QUESTION_TYPE, EvaluationStrategy_V13Constants.QuestionType.GOOD_TO_KNOW,
					RATING_LOWER_BOUND, 8D,
					RATING_UPPER_BOUND, 9D,
					MODIFIED_WEIGHTAGE, 1.25D),
			Map.of(
					QUESTION_TYPE, EvaluationStrategy_V13Constants.QuestionType.GOOD_TO_KNOW,
					RATING_LOWER_BOUND, 7D,
					RATING_UPPER_BOUND, 8D,
					MODIFIED_WEIGHTAGE, 1D),
			Map.of(
					QUESTION_TYPE, EvaluationStrategy_V13Constants.QuestionType.GOOD_TO_KNOW,
					RATING_LOWER_BOUND, 6D,
					RATING_UPPER_BOUND, 7D,
					MODIFIED_WEIGHTAGE, 0.75D),
			Map.of(
					QUESTION_TYPE, EvaluationStrategy_V13Constants.QuestionType.GOOD_TO_KNOW,
					RATING_LOWER_BOUND, 5D,
					RATING_UPPER_BOUND, 6D,
					MODIFIED_WEIGHTAGE, 0.5D),
			Map.of(
					QUESTION_TYPE, EvaluationStrategy_V13Constants.QuestionType.GOOD_TO_KNOW,
					RATING_LOWER_BOUND, 4.999D,
					RATING_UPPER_BOUND, 5D,
					MODIFIED_WEIGHTAGE, 0.25D));

	public static Double getModifiedWeightage(final EvaluationStrategy_V13Constants.QuestionType questionType,
			final Float rating) {
		final double roundedRating = Math.floor(rating * 1000) / 1000D;
		for (Map<String, Object> obj : questionTypeToModifiedWeightageMapping) {
			if (questionType.equals(obj.get(QUESTION_TYPE)) && roundedRating > (Double) obj.get(RATING_LOWER_BOUND) &&
					rating <= (Double) obj.get(RATING_UPPER_BOUND)) {
				return (Double) obj.get(MODIFIED_WEIGHTAGE);
			}
		}
		return DEFAULT_MODIFIED_WEIGHTAGE;
	}
}
