/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores.v10;

import com.barraiser.onboarding.interview.pojo.FeedbackData;
import com.barraiser.onboarding.interview.pojo.QuestionData;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class FeedbackLengthFlagCalculator_V10 {
	public static final Integer MIN_NUMBER_OF_QUESTIONS = 3;

	public List<QuestionData> calculate(List<QuestionData> questionsOfInterview) {
		final List<FeedbackData> feedbacks = questionsOfInterview.stream().map(QuestionData::getFeedbacks)
				.flatMap(Collection::stream).collect(Collectors.toList());
		if (questionsOfInterview.size() <= MIN_NUMBER_OF_QUESTIONS) {
			this.populateFeedbackFlagsBasedOnSD(feedbacks);
		} else {
			this.populateFeedbackFlagsBasedOnPercentile(feedbacks);
		}
		return this.assignFeedbacksBackToQuestion(questionsOfInterview, feedbacks);
	}

	private void populateFeedbackFlagsBasedOnSD(final List<FeedbackData> feedbacks) {
		final double mean = feedbacks.stream().mapToDouble(x -> x.getFeedback().length()).sum()
				/ feedbacks.size();
		final double squaredDiff = feedbacks.stream().mapToDouble(x -> Math.pow(x.getFeedback().length() - mean, 2))
				.sum();
		final double standardDeviation = Math.sqrt(squaredDiff / feedbacks.size());
		for (int i = 0; i < feedbacks.size(); i++) {
			final String feedbackLengthFlag = this
					.getFeedbackLengthBasedOnSD(feedbacks.get(i).getFeedback().length(), mean, standardDeviation)
					.getValue();
			final FeedbackData feedbackData = feedbacks.get(i).toBuilder()
					.lengthFlag(feedbackLengthFlag)
					.build();
			feedbacks.set(i, feedbackData);
		}
	}

	private void populateFeedbackFlagsBasedOnPercentile(final List<FeedbackData> feedbacks) {
		feedbacks.sort(Comparator.comparing(x -> x.getFeedback().length()));
		final double indexFor33percentile = Math.ceil(((double) 33 / 100) * (double) feedbacks.size()) - 1;
		final double indexFor66percentile = Math.ceil(((double) 66 / 100) * (double) feedbacks.size()) - 1;
		for (int i = 0; i < feedbacks.size(); i++) {
			final FeedbackData feedbackData = feedbacks.get(i).toBuilder()
					.lengthFlag(this.getFeedbackLengthBasedOnPercentile(i, (int) indexFor33percentile,
							(int) indexFor66percentile).getValue())
					.build();
			feedbacks.set(i, feedbackData);
		}
	}

	private List<QuestionData> assignFeedbacksBackToQuestion(final List<QuestionData> questions,
			final List<FeedbackData> feedbacks) {
		for (int i = 0; i < questions.size(); i++) {
			QuestionData questionData = questions.get(i);
			final String referenceId = questionData.getId();
			final List<FeedbackData> feedbackOfQuestion = feedbacks.stream()
					.filter(x -> x.getReferenceId().equals(referenceId)).collect(Collectors.toList());
			questionData = questionData.toBuilder().feedbacks(feedbackOfQuestion).build();
			questions.set(i, questionData);
		}
		return questions;
	}

	private EvaluationStrategy_V10Constants.FeedbackLengthFlag getFeedbackLengthBasedOnSD(final Integer feedbackLength,
			final Double mean, final Double standardDeviation) {
		return feedbackLength >= mean - (standardDeviation / 2)
				? feedbackLength > mean + (standardDeviation / 6)
						? EvaluationStrategy_V10Constants.FeedbackLengthFlag.HIGH
						: EvaluationStrategy_V10Constants.FeedbackLengthFlag.MID
				: EvaluationStrategy_V10Constants.FeedbackLengthFlag.LOW;
	}

	private EvaluationStrategy_V10Constants.FeedbackLengthFlag getFeedbackLengthBasedOnPercentile(
			final Integer sortedIndexOfFeedbackLength,
			final Integer indexFor33percentile, final Integer indexFor66percentile) {
		return sortedIndexOfFeedbackLength > indexFor66percentile
				? EvaluationStrategy_V10Constants.FeedbackLengthFlag.HIGH
				: sortedIndexOfFeedbackLength < indexFor33percentile
						? EvaluationStrategy_V10Constants.FeedbackLengthFlag.LOW
						: EvaluationStrategy_V10Constants.FeedbackLengthFlag.MID;
	}
}
