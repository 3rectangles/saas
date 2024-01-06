/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.feeback.evaluation;

import com.barraiser.common.graphql.types.Question;
import com.barraiser.onboarding.interview.InterviewStructureManager;
import com.barraiser.onboarding.interview.QuestionUtil;
import com.barraiser.onboarding.interview.evaluation.scores.EvaluationStrategy_V5Constants;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class HigherBGSDoesNotMatchDifficultyCriteria implements FeedbackEvaluator {
	private final InterviewStructureManager interviewStructureManager;

	@Override
	public List<String> getImprovement(final FeedbackSummaryData data) {
		return Arrays.asList(this.doesHigherDifficultyAndNumberOfHardQuestionsMatch(data));
	}

	@Override
	public int order() {
		return 5;
	}

	private String doesHigherDifficultyAndNumberOfHardQuestionsMatch(
			final FeedbackSummaryData data) {
		if (this.interviewStructureManager.isInterviewStructureOnNewFlow(data.getInterviewFlowVersion())) {
			return null;
		}
		final List<Question> questionsToBeConsidered = data.getAllQuestions().stream()
				.filter(
						x -> !EvaluationStrategy_V5Constants.QuestionType.NON_EVALUATIVE
								.getValue()
								.equals(x.getType())
								&& !EvaluationStrategy_V5Constants.QuestionType.DELETED
										.getValue()
										.equals(x.getType()))
				.collect(Collectors.toList());
		final Integer questionsAskedForHardDifficulty = QuestionUtil.getNumberOfQuestionsAskedInDifficulty(
				EvaluationStrategy_V5Constants.FeedbackDifficulty.HARD.getValue(),
				questionsToBeConsidered);
		if (data.getBgsScore() > FeedbackSummaryConstants.HIGHER_BOUND_FOR_BGS_SCORE) {
			final Double ratioOfHardQuestionsToTotalQuestions = (double) questionsAskedForHardDifficulty
					/ (double) questionsToBeConsidered.size();
			if (ratioOfHardQuestionsToTotalQuestions == 0) {
				return FeedbackSummaryConstants.NO_HARD_QUESTIONS_ASKED_IN_HIGHER_BGS_IMPROVEMENT_MESSAGE;
			} else if (ratioOfHardQuestionsToTotalQuestions < FeedbackSummaryConstants.RATIO_OF_HARD_QUESTIONS_TO_TOTAL_QUESTIONS_THRESHOLD) {
				return String.format(
						FeedbackSummaryConstants.HIGHER_BGS_DOES_NOT_MATCH_DIFFICULTY_CRITERIA_IMPROVEMENT_MESSAGE,
						Math.round(ratioOfHardQuestionsToTotalQuestions * 100));
			}
		}
		return null;
	}
}
