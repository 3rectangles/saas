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
public class FirstQuestionTypeCheckCriteria implements FeedbackEvaluator {
	@Override
	public List<String> getImprovement(final FeedbackSummaryData data) {
		return Arrays.asList(this.checkIfFirstQuestionIsRequiredOrNot(data.getAllQuestions()));
	}

	@Override
	public int order() {
		return 3;
	}

	public String checkIfFirstQuestionIsRequiredOrNot(final List<Question> questions) {
		final String questionTypeOfFirstQuestion = questions.stream()
				.filter(
						x -> !EvaluationStrategy_V5Constants.QuestionType.DELETED
								.getValue()
								.equals(x.getType()))
				.findFirst()
				.get()
				.getType();
		if (questionTypeOfFirstQuestion.equals(
				EvaluationStrategy_V5Constants.QuestionType.GOOD_TO_KNOW.getValue())) {
			return FeedbackSummaryConstants.FIRST_QUESTION_TYPE_CHECK_CRITERIA_IMPROVEMENT_MESSAGE;
		}
		return null;
	}
}
