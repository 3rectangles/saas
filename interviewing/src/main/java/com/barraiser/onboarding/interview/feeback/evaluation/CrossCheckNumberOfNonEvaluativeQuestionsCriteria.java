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
public class CrossCheckNumberOfNonEvaluativeQuestionsCriteria implements FeedbackEvaluator {
	@Override
	public List<String> getImprovement(final FeedbackSummaryData data) {
		return Arrays.asList(
				this.isGreaterNumberOfNonEvaluativeQuestionsAsked(data.getAllQuestions()));
	}

	@Override
	public int order() {
		return 6;
	}

	private String isGreaterNumberOfNonEvaluativeQuestionsAsked(final List<Question> questions) {
		final Long numberOfNonEvaluativeQuestionsAsked = questions.stream()
				.filter(
						x -> EvaluationStrategy_V5Constants.QuestionType.NON_EVALUATIVE
								.getValue()
								.equals(x.getType()))
				.count();
		if ((double) numberOfNonEvaluativeQuestionsAsked / (double) questions
				.size() > FeedbackSummaryConstants.RATIO_OF_NON_EVALUATIVE_QUESTIONS_TO_TOTAL_QUESTIONS_THRESHOLD
				|| numberOfNonEvaluativeQuestionsAsked > FeedbackSummaryConstants.TOTAL_NUMBER_OF_NON_EVALUATIVE_QUESTIONS_THRESHOLD) {
			return String.format(
					FeedbackSummaryConstants.NON_EVALUATIVE_QUESTION_CRITERIA_IMPROVEMENT_MESSAGE,
					numberOfNonEvaluativeQuestionsAsked);
		}
		return null;
	}
}
