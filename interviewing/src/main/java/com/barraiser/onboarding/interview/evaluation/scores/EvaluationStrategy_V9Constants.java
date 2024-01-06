/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores;

import java.util.NoSuchElementException;

public class EvaluationStrategy_V9Constants {

	public enum QuestionType {
		REQUIRED("REQUIRED"), GOOD_TO_KNOW("GOOD_TO_KNOW"), NON_EVALUATIVE("NON_EVALUATIVE"), DELETED("DELETED");

		private final String questionType;

		QuestionType(final String questionType) {
			this.questionType = questionType;
		}

		public String getValue() {
			return this.questionType;
		}

		public static EvaluationStrategy_V9Constants.QuestionType fromString(
				final String difficulty) {
			for (final EvaluationStrategy_V9Constants.QuestionType qt : values()) {
				if (qt.getValue().equals(difficulty)) {
					return qt;
				}
			}
			throw new NoSuchElementException(
					"Element with value " + difficulty + " has not been found");
		}
	}

	public enum FeedbackDifficulty {
		HARD("HARD"), MODERATE("MODERATE"), EASY("EASY");

		private final String difficulty;

		FeedbackDifficulty(final String difficulty) {
			this.difficulty = difficulty;
		}

		public String getValue() {
			return this.difficulty;
		}

		public static EvaluationStrategy_V9Constants.FeedbackDifficulty fromString(
				final String status) {
			for (final EvaluationStrategy_V9Constants.FeedbackDifficulty fd : values()) {
				if (fd.getValue().equals(status)) {
					return fd;
				}
			}
			throw new NoSuchElementException(
					"Element with value " + status + " has not been found");
		}
	}
}
