/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores;

import java.util.Map;
import java.util.NoSuchElementException;

public class EvaluationStrategy_V5Constants {

	public static class ModifiedWeightage {
		public static Map<String, Double> difficultyMap = Map.ofEntries(
				Map.entry("EASY_1.0", 3D),
				Map.entry("EASY_2.0", 3D),
				Map.entry("EASY_3.0", 3D),
				Map.entry("EASY_4.0", 2.5D),
				Map.entry("EASY_5.0", 2.5D),
				Map.entry("EASY_6.0", 1D),
				Map.entry("EASY_7.0", 1D),
				Map.entry("EASY_8.0", 1D),
				Map.entry("EASY_9.0", 1.5D),
				Map.entry("EASY_10.0", 1.5D),
				Map.entry("MODERATE_1.0", 1.5D),
				Map.entry("MODERATE_2.0", 1.4D),
				Map.entry("MODERATE_3.0", 1.3D),
				Map.entry("MODERATE_4.0", 1.2D),
				Map.entry("MODERATE_5.0", 1.1D),
				Map.entry("MODERATE_6.0", 1D),
				Map.entry("MODERATE_7.0", 1.25D),
				Map.entry("MODERATE_8.0", 1.5D),
				Map.entry("MODERATE_9.0", 1.75D),
				Map.entry("MODERATE_10.0", 2D),
				Map.entry("HARD_1.0", 1D),
				Map.entry("HARD_2.0", 1D),
				Map.entry("HARD_3.0", 1D),
				Map.entry("HARD_4.0", 1D),
				Map.entry("HARD_5.0", 1D),
				Map.entry("HARD_6.0", 2.5D),
				Map.entry("HARD_7.0", 2.5D),
				Map.entry("HARD_8.0", 2.5D),
				Map.entry("HARD_9.0", 3D),
				Map.entry("HARD_10.0", 3D));

		public static Map<String, Double> questionTypeMap = Map.ofEntries(
				Map.entry("REQUIRED_1.0", 1D),
				Map.entry("REQUIRED_2.0", 1D),
				Map.entry("REQUIRED_3.0", 1D),
				Map.entry("REQUIRED_4.0", 1D),
				Map.entry("REQUIRED_5.0", 1D),
				Map.entry("REQUIRED_6.0", 1D),
				Map.entry("REQUIRED_7.0", 1D),
				Map.entry("REQUIRED_8.0", 1D),
				Map.entry("REQUIRED_9.0", 1D),
				Map.entry("REQUIRED_10.0", 1D),
				Map.entry("GOOD_TO_KNOW_1.0", 0D),
				Map.entry("GOOD_TO_KNOW_2.0", 0D),
				Map.entry("GOOD_TO_KNOW_3.0", 0D),
				Map.entry("GOOD_TO_KNOW_4.0", 0D),
				Map.entry("GOOD_TO_KNOW_5.0", 0.25D),
				Map.entry("GOOD_TO_KNOW_6.0", 0.5D),
				Map.entry("GOOD_TO_KNOW_7.0", 0.75D),
				Map.entry("GOOD_TO_KNOW_8.0", 1D),
				Map.entry("GOOD_TO_KNOW_9.0", 1.25D),
				Map.entry("GOOD_TO_KNOW_10.0", 1.5D));
	}

	public enum QuestionType {
		REQUIRED("REQUIRED"), GOOD_TO_KNOW("GOOD_TO_KNOW"), NON_EVALUATIVE("NON_EVALUATIVE"), DELETED("DELETED");

		private final String questionType;

		QuestionType(final String questionType) {
			this.questionType = questionType;
		}

		public String getValue() {
			return this.questionType;
		}

		public static QuestionType fromString(final String difficulty) {
			for (final QuestionType qt : values()) {
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

		public static FeedbackDifficulty fromString(final String status) {
			for (final FeedbackDifficulty fd : values()) {
				if (fd.getValue().equals(status)) {
					return fd;
				}
			}
			throw new NoSuchElementException(
					"Element with value " + status + " has not been found");
		}
	}
}
