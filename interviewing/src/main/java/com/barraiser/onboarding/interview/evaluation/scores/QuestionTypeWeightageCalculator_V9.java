/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores;

public class QuestionTypeWeightageCalculator_V9 {

	public static Double getModifiedWeightage(final String questionType, final Float rating) {
		if (questionType.equals(EvaluationStrategy_V5Constants.QuestionType.REQUIRED.getValue())) {
			return 1D;
		} else if (questionType.equals(
				EvaluationStrategy_V5Constants.QuestionType.GOOD_TO_KNOW.getValue())
				&& rating > 9) {
			return 1.5D;
		} else if (questionType.equals(
				EvaluationStrategy_V5Constants.QuestionType.GOOD_TO_KNOW.getValue())
				&& rating > 8) {
			return 1.25D;
		} else if (questionType.equals(
				EvaluationStrategy_V5Constants.QuestionType.GOOD_TO_KNOW.getValue())
				&& rating > 7) {
			return 1D;
		} else if (questionType.equals(
				EvaluationStrategy_V5Constants.QuestionType.GOOD_TO_KNOW.getValue())
				&& rating > 6) {
			return 0.75D;
		} else if (questionType.equals(
				EvaluationStrategy_V5Constants.QuestionType.GOOD_TO_KNOW.getValue())
				&& rating > 5) {
			return 0.5D;
		} else if (questionType.equals(
				EvaluationStrategy_V5Constants.QuestionType.GOOD_TO_KNOW.getValue())
				&& rating == 5) {
			return 0.25D;
		} else {
			return 0D;
		}
	}
}
