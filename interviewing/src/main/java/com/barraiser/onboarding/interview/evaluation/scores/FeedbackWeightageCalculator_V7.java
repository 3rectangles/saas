/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class FeedbackWeightageCalculator_V7 {

	public static Double getModifiedWeightage(final String difficulty, final Float rating) {

		if (difficulty.equals(EvaluationStrategy_V5Constants.FeedbackDifficulty.HARD.getValue())
				&& rating > 8) {
			return 2D;
		} else if (difficulty.equals(
				EvaluationStrategy_V5Constants.FeedbackDifficulty.HARD.getValue())
				&& rating >= 6
				&& rating <= 8) {
			return 1.5D;
		} else if (difficulty.equals(
				EvaluationStrategy_V5Constants.FeedbackDifficulty.MODERATE.getValue())
				&& rating <= 1) {
			return 1.5D;
		} else if (difficulty.equals(
				EvaluationStrategy_V5Constants.FeedbackDifficulty.MODERATE.getValue())
				&& rating <= 2) {
			return 1.4D;
		} else if (difficulty.equals(
				EvaluationStrategy_V5Constants.FeedbackDifficulty.MODERATE.getValue())
				&& rating <= 3) {
			return 1.3D;
		} else if (difficulty.equals(
				EvaluationStrategy_V5Constants.FeedbackDifficulty.MODERATE.getValue())
				&& rating <= 4) {
			return 1.2D;
		} else if (difficulty.equals(
				EvaluationStrategy_V5Constants.FeedbackDifficulty.MODERATE.getValue())
				&& rating <= 5) {
			return 1.1D;
		} else if (difficulty.equals(
				EvaluationStrategy_V5Constants.FeedbackDifficulty.MODERATE.getValue())
				&& rating <= 6) {
			return 1D;
		} else if (difficulty.equals(
				EvaluationStrategy_V5Constants.FeedbackDifficulty.MODERATE.getValue())
				&& rating <= 7) {
			return 1.25D;
		} else if (difficulty.equals(
				EvaluationStrategy_V5Constants.FeedbackDifficulty.MODERATE.getValue())
				&& rating <= 8) {
			return 1.5D;
		} else if (difficulty.equals(
				EvaluationStrategy_V5Constants.FeedbackDifficulty.MODERATE.getValue())
				&& rating <= 9) {
			return 1.75D;
		} else if (difficulty.equals(
				EvaluationStrategy_V5Constants.FeedbackDifficulty.MODERATE.getValue())
				&& rating <= 10) {
			return 2D;
		} else if (difficulty.equals(
				EvaluationStrategy_V5Constants.FeedbackDifficulty.EASY.getValue())
				&& rating >= 9
				&& rating <= 10) {
			return 1.5D;
		} else if (difficulty.equals(
				EvaluationStrategy_V5Constants.FeedbackDifficulty.EASY.getValue())
				&& rating <= 5) {
			return 1.5D;
		} else {
			return 1D;
		}
	}
}
