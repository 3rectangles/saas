/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores.v11;

import java.util.List;
import java.util.Map;

public class QuestionTimeSpentFlagCalculator_V11 {
	public static final String TIME_SPENT_LOWER_BOUND = "time_spent_lower_bound";
	public static final String TIME_SPENT_UPPER_BOUND = "time_spent_upper_bound";
	public static final String TIME_SPENT_FLAG = "time_spent_flag";
	public static final EvaluationStrategy_V11Constants.QuestionTimeSpentFlag DEFAULT_TIME_SPENT_FLAG = EvaluationStrategy_V11Constants.QuestionTimeSpentFlag.MID;

	private static final List<Map<String, Object>> questionTimeToFlagMapping = List.of(
			Map.of(
					TIME_SPENT_LOWER_BOUND, Integer.MIN_VALUE,
					TIME_SPENT_UPPER_BOUND, 0,
					TIME_SPENT_FLAG, EvaluationStrategy_V11Constants.QuestionTimeSpentFlag.MID),
			Map.of(
					TIME_SPENT_LOWER_BOUND, 0,
					TIME_SPENT_UPPER_BOUND, 90,
					TIME_SPENT_FLAG, EvaluationStrategy_V11Constants.QuestionTimeSpentFlag.VERY_LOW),
			Map.of(
					TIME_SPENT_LOWER_BOUND, 90,
					TIME_SPENT_UPPER_BOUND, 180,
					TIME_SPENT_FLAG, EvaluationStrategy_V11Constants.QuestionTimeSpentFlag.LOW),
			Map.of(
					TIME_SPENT_LOWER_BOUND, 180,
					TIME_SPENT_UPPER_BOUND, 360,
					TIME_SPENT_FLAG, EvaluationStrategy_V11Constants.QuestionTimeSpentFlag.MID),
			Map.of(
					TIME_SPENT_LOWER_BOUND, 360,
					TIME_SPENT_UPPER_BOUND, 600,
					TIME_SPENT_FLAG, EvaluationStrategy_V11Constants.QuestionTimeSpentFlag.HIGH),
			Map.of(
					TIME_SPENT_LOWER_BOUND, 600,
					TIME_SPENT_UPPER_BOUND, Integer.MAX_VALUE,
					TIME_SPENT_FLAG, EvaluationStrategy_V11Constants.QuestionTimeSpentFlag.VERY_HIGH));

	public static EvaluationStrategy_V11Constants.QuestionTimeSpentFlag calculate(final Long startTime,
			final Long endTime) {
		if (startTime != null && endTime != null) {
			final Long totalTimeSpent = endTime - startTime;
			for (final Map<String, Object> obj : questionTimeToFlagMapping) {
				if (totalTimeSpent > (Integer) obj.get(TIME_SPENT_LOWER_BOUND)
						&& totalTimeSpent <= (Integer) obj.get(TIME_SPENT_UPPER_BOUND)) {
					return (EvaluationStrategy_V11Constants.QuestionTimeSpentFlag) obj.get(TIME_SPENT_FLAG);
				}
			}
		}
		return DEFAULT_TIME_SPENT_FLAG;
	}
}
