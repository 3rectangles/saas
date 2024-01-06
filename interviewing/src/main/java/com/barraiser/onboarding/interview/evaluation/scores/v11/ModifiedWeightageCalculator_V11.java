/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores.v11;

import java.util.List;
import java.util.Map;

public class ModifiedWeightageCalculator_V11 {
	public static final String QUESTION_TIME_SPENT_FLAG = "question_time_spent_flag";
	public static final String FEEDBACK_LENGTH_FLAG = "feedback_length_flag";
	public static final String MODIFIED_WEIGHTAGE = "modified_weightage";
	public static Double DEFAULT_MODIFIED_WEIGHTAGE = 2D;
	private static final List<Map<String, Object>> flagToModifiedWeightageMapping = List.of(
			Map.of(
					QUESTION_TIME_SPENT_FLAG, EvaluationStrategy_V11Constants.QuestionTimeSpentFlag.VERY_HIGH,
					FEEDBACK_LENGTH_FLAG, EvaluationStrategy_V11Constants.FeedbackLengthFlag.HIGH,
					MODIFIED_WEIGHTAGE, 4D),
			Map.of(
					QUESTION_TIME_SPENT_FLAG, EvaluationStrategy_V11Constants.QuestionTimeSpentFlag.VERY_HIGH,
					FEEDBACK_LENGTH_FLAG, EvaluationStrategy_V11Constants.FeedbackLengthFlag.MID,
					MODIFIED_WEIGHTAGE, 3D),
			Map.of(
					QUESTION_TIME_SPENT_FLAG, EvaluationStrategy_V11Constants.QuestionTimeSpentFlag.HIGH,
					FEEDBACK_LENGTH_FLAG, EvaluationStrategy_V11Constants.FeedbackLengthFlag.HIGH,
					MODIFIED_WEIGHTAGE, 3D),
			Map.of(
					QUESTION_TIME_SPENT_FLAG, EvaluationStrategy_V11Constants.QuestionTimeSpentFlag.HIGH,
					FEEDBACK_LENGTH_FLAG, EvaluationStrategy_V11Constants.FeedbackLengthFlag.MID,
					MODIFIED_WEIGHTAGE, 3D),
			Map.of(
					QUESTION_TIME_SPENT_FLAG, EvaluationStrategy_V11Constants.QuestionTimeSpentFlag.LOW,
					FEEDBACK_LENGTH_FLAG, EvaluationStrategy_V11Constants.FeedbackLengthFlag.LOW,
					MODIFIED_WEIGHTAGE, 1.5D),
			Map.of(
					QUESTION_TIME_SPENT_FLAG, EvaluationStrategy_V11Constants.QuestionTimeSpentFlag.VERY_LOW,
					FEEDBACK_LENGTH_FLAG, EvaluationStrategy_V11Constants.FeedbackLengthFlag.MID,
					MODIFIED_WEIGHTAGE, 1.5D),
			Map.of(
					QUESTION_TIME_SPENT_FLAG, EvaluationStrategy_V11Constants.QuestionTimeSpentFlag.VERY_LOW,
					FEEDBACK_LENGTH_FLAG, EvaluationStrategy_V11Constants.FeedbackLengthFlag.LOW,
					MODIFIED_WEIGHTAGE, 1D));

	public static Double getModifiedWeightageForFeedbackLengthAndQuestionTime(
			final EvaluationStrategy_V11Constants.FeedbackLengthFlag feedbackLengthFlag,
			final EvaluationStrategy_V11Constants.QuestionTimeSpentFlag questionTimeFlag) {

		for (final Map<String, Object> obj : flagToModifiedWeightageMapping) {
			if (feedbackLengthFlag.equals(obj.get(FEEDBACK_LENGTH_FLAG))
					&& questionTimeFlag.equals(obj.get(QUESTION_TIME_SPENT_FLAG))) {
				return (Double) obj.get(MODIFIED_WEIGHTAGE);
			}
		}
		return DEFAULT_MODIFIED_WEIGHTAGE;
	}
}
