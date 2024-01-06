/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.feeback.evaluation;

import com.barraiser.common.graphql.types.Feedback;
import com.barraiser.common.graphql.types.Question;
import com.barraiser.onboarding.common.Constants;
import com.barraiser.onboarding.interview.InterviewStructureManager;
import com.barraiser.onboarding.interview.evaluation.scores.EvaluationStrategy_V5Constants;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class SingleDifficultyMentionedCriteria implements FeedbackEvaluator {
	private final InterviewStructureManager interviewStructureManager;

	@Override
	public List<String> getImprovement(final FeedbackSummaryData data) {
		return Arrays.asList(this.doAllQuestionsBelongToSameDifficulty(data));
	}

	@Override
	public int order() {
		return 4;
	}

	private String doAllQuestionsBelongToSameDifficulty(final FeedbackSummaryData data) {
		if (this.interviewStructureManager.isInterviewStructureOnNewFlow(data.getInterviewFlowVersion())) {
			return null;
		}
		final List<String> difficulties = data.getAllQuestions().stream()
				.filter(
						x -> !EvaluationStrategy_V5Constants.QuestionType.NON_EVALUATIVE
								.getValue()
								.equals(x.getType())
								&& !EvaluationStrategy_V5Constants.QuestionType.DELETED
										.getValue()
										.equals(x.getType()))
				.map(Question::getFeedbacks)
				.flatMap(Collection::stream)
				.filter(y -> !Constants.SOFT_SKILL_ID.equals(y.getCategoryId()))
				.map(Feedback::getDifficulty)
				.distinct()
				.collect(Collectors.toList());
		if (difficulties.size() == 1) {
			return String.format(
					FeedbackSummaryConstants.SINGLE_DIFFICULTY_MENTIONED_CRITERIA_IMPROVEMENT_MESSAGE,
					difficulties.get(0).toLowerCase());
		}
		return null;
	}
}
