/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores.v13;

import java.util.NoSuchElementException;

public class EvaluationStrategy_V13Constants {

	public enum FeedbackLengthFlag {
		HIGH("HIGH"), MID("MID"), LOW("LOW");

		private final String feedbackLengthFlag;

		FeedbackLengthFlag(final String feedbackLengthFlag) {
			this.feedbackLengthFlag = feedbackLengthFlag;
		}

		public String getValue() {
			return this.feedbackLengthFlag;
		}

		public static FeedbackLengthFlag fromString(
				final String feedbackLengthFlag) {
			for (final FeedbackLengthFlag lf : values()) {
				if (lf.getValue().equals(feedbackLengthFlag)) {
					return lf;
				}
			}
			throw new NoSuchElementException(
					"Element with value " + feedbackLengthFlag + " has not been found");
		}
	}

	public enum QuestionTimeSpentFlag {
		VERY_HIGH("VERY_HIGH"), HIGH("HIGH"), MID("MID"), LOW("LOW"), VERY_LOW("VERY_LOW");

		private final String questionTimeSpentFlag;

		QuestionTimeSpentFlag(final String questionTimeSpentFlag) {
			this.questionTimeSpentFlag = questionTimeSpentFlag;
		}

		public String getValue() {
			return this.questionTimeSpentFlag;
		}

		public static QuestionTimeSpentFlag fromString(
				final String questionTimeSpentFlag) {
			for (final QuestionTimeSpentFlag lf : values()) {
				if (lf.getValue().equals(questionTimeSpentFlag)) {
					return lf;
				}
			}
			throw new NoSuchElementException(
					"Element with value " + questionTimeSpentFlag + " has not been found");
		}
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

		public static QuestionType fromString(
				final String difficulty) {
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

		public static FeedbackDifficulty fromString(
				final String status) {
			if (status == null) {
				return null;
			}
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
