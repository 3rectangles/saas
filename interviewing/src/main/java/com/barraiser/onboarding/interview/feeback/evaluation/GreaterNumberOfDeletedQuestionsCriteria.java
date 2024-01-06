/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.feeback.evaluation;

import com.barraiser.common.graphql.types.Question;
import com.barraiser.onboarding.interview.evaluation.scores.EvaluationStrategy_V5Constants;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@AllArgsConstructor
public class GreaterNumberOfDeletedQuestionsCriteria implements FeedbackEvaluator {
	@Override
	public List<String> getImprovement(final FeedbackSummaryData data) {
		return Arrays.asList(
				this.checkIfGreaterNumberOfDeletedQuestionsPresent(data.getAllQuestions()));
	}

	@Override
	public int order() {
		return 8;
	}

	private String checkIfGreaterNumberOfDeletedQuestionsPresent(final List<Question> questions) {
		final Integer numberOfDeletedQuestions = (int) questions.stream()
				.filter(
						x -> EvaluationStrategy_V5Constants.QuestionType.DELETED
								.getValue()
								.equals(x.getType()))
				.count();
		if (numberOfDeletedQuestions > FeedbackSummaryConstants.NUMBER_OF_DELETED_QUESTIONS_THRESHOLD) {
			return String.format(
					FeedbackSummaryConstants.GREATER_NUMBER_OF_DELETED_QUESTIONS_IMPROVEMENT_MESSAGE,
					numberOfDeletedQuestions);
		}
		return null;
	}
}
