/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores.v13;

import com.barraiser.onboarding.interview.pojo.FeedbackData;

import java.util.List;
import java.util.Map;

public class ModifiedWeightageCalculator_V13 {
	public static final String QUESTION_TIME_SPENT_FLAG = "question_time_spent_flag";
	public static final String FEEDBACK_LENGTH_FLAG = "feedback_length_flag";
	public static final String MODIFIED_WEIGHTAGE = "modified_weightage";
	public static Double DEFAULT_MODIFIED_WEIGHTAGE = 2D;
	private static final List<Map<String, Object>> saasFlagToModifiedWeightageMapping = List.of(
			Map.of(
					QUESTION_TIME_SPENT_FLAG, EvaluationStrategy_V13Constants.QuestionTimeSpentFlag.VERY_HIGH,
					MODIFIED_WEIGHTAGE, 4D),
			Map.of(
					QUESTION_TIME_SPENT_FLAG, EvaluationStrategy_V13Constants.QuestionTimeSpentFlag.HIGH,
					MODIFIED_WEIGHTAGE, 3D),
			Map.of(
					QUESTION_TIME_SPENT_FLAG, EvaluationStrategy_V13Constants.QuestionTimeSpentFlag.MID,
					MODIFIED_WEIGHTAGE, 2D),
			Map.of(
					QUESTION_TIME_SPENT_FLAG, EvaluationStrategy_V13Constants.QuestionTimeSpentFlag.LOW,
					MODIFIED_WEIGHTAGE, 2D),
			Map.of(
					QUESTION_TIME_SPENT_FLAG, EvaluationStrategy_V13Constants.QuestionTimeSpentFlag.VERY_LOW,
					MODIFIED_WEIGHTAGE, 2D));
	private static final List<Map<String, Object>> iaasFlagToModifiedWeightageMapping = List.of(
			Map.of(
					QUESTION_TIME_SPENT_FLAG, EvaluationStrategy_V13Constants.QuestionTimeSpentFlag.VERY_HIGH,
					FEEDBACK_LENGTH_FLAG, EvaluationStrategy_V13Constants.FeedbackLengthFlag.HIGH,
					MODIFIED_WEIGHTAGE, 4D),
			Map.of(
					QUESTION_TIME_SPENT_FLAG, EvaluationStrategy_V13Constants.QuestionTimeSpentFlag.VERY_HIGH,
					FEEDBACK_LENGTH_FLAG, EvaluationStrategy_V13Constants.FeedbackLengthFlag.MID,
					MODIFIED_WEIGHTAGE, 3D),
			Map.of(
					QUESTION_TIME_SPENT_FLAG, EvaluationStrategy_V13Constants.QuestionTimeSpentFlag.HIGH,
					FEEDBACK_LENGTH_FLAG, EvaluationStrategy_V13Constants.FeedbackLengthFlag.HIGH,
					MODIFIED_WEIGHTAGE, 3D),
			Map.of(
					QUESTION_TIME_SPENT_FLAG, EvaluationStrategy_V13Constants.QuestionTimeSpentFlag.HIGH,
					FEEDBACK_LENGTH_FLAG, EvaluationStrategy_V13Constants.FeedbackLengthFlag.MID,
					MODIFIED_WEIGHTAGE, 3D),
			Map.of(
					QUESTION_TIME_SPENT_FLAG, EvaluationStrategy_V13Constants.QuestionTimeSpentFlag.LOW,
					FEEDBACK_LENGTH_FLAG, EvaluationStrategy_V13Constants.FeedbackLengthFlag.LOW,
					MODIFIED_WEIGHTAGE, 1.5D),
			Map.of(
					QUESTION_TIME_SPENT_FLAG, EvaluationStrategy_V13Constants.QuestionTimeSpentFlag.VERY_LOW,
					FEEDBACK_LENGTH_FLAG, EvaluationStrategy_V13Constants.FeedbackLengthFlag.MID,
					MODIFIED_WEIGHTAGE, 1.5D),
			Map.of(
					QUESTION_TIME_SPENT_FLAG, EvaluationStrategy_V13Constants.QuestionTimeSpentFlag.VERY_LOW,
					FEEDBACK_LENGTH_FLAG, EvaluationStrategy_V13Constants.FeedbackLengthFlag.LOW,
					MODIFIED_WEIGHTAGE, 1D));

	public static Double getModifiedWeightageForFeedbackLengthAndQuestionTime(
			final FeedbackData feedback,
			final EvaluationStrategy_V13Constants.QuestionTimeSpentFlag questionTimeFlag) {
		if (feedback.getIsSaasFeedback()) {
			for (final Map<String, Object> obj : saasFlagToModifiedWeightageMapping) {
				if (questionTimeFlag.equals(obj.get(QUESTION_TIME_SPENT_FLAG))) {
					return (Double) obj.get(MODIFIED_WEIGHTAGE);
				}
			}
		} else {
			final EvaluationStrategy_V13Constants.FeedbackLengthFlag feedbackLengthFlag = EvaluationStrategy_V13Constants.FeedbackLengthFlag
					.fromString(feedback.getLengthFlag());
			for (final Map<String, Object> obj : iaasFlagToModifiedWeightageMapping) {
				if (feedbackLengthFlag.equals(obj.get(FEEDBACK_LENGTH_FLAG))
						&& questionTimeFlag.equals(obj.get(QUESTION_TIME_SPENT_FLAG))) {
					return (Double) obj.get(MODIFIED_WEIGHTAGE);
				}
			}
		}
		return DEFAULT_MODIFIED_WEIGHTAGE;
	}
}
