/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores.v10;

import com.barraiser.common.graphql.types.SkillScore;
import com.barraiser.onboarding.interview.evaluation.scores.*;
import com.barraiser.onboarding.interview.feeback.FeedbackNormalisationUtil;
import com.barraiser.onboarding.interview.pojo.FeedbackData;
import com.barraiser.onboarding.interview.pojo.InterviewData;
import com.barraiser.onboarding.interview.pojo.QuestionData;
import com.barraiser.onboarding.interview.evaluation.EvaluationScoreComputationManager;
import com.barraiser.onboarding.interview.evaluation.scores.pojo.ComputeEvaluationScoresData;
import com.barraiser.onboarding.interview.evaluation.scores.pojo.EvaluationScoreData;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Component
@AllArgsConstructor
public class EvaluationStrategy_V10 implements EvaluationStrategy {
	public static final String SCORING_ALGO_VERSION = "10";

	private final EvaluationScoreComputationManager evaluationScoreComputationManager;
	private final FeedbackLengthFlagCalculator_V10 feedbackLengthFlagCalculator_v10;
	private final FeedbackNormalisationUtil feedbackNormalisationUtil;

	@Override
	public String version() {
		return SCORING_ALGO_VERSION;
	}

	@Override
	public EvaluationScoreData computeEvaluationScore(final ComputeEvaluationScoresData input) {
		final Map<String, Double> skillWeightageMap = input.getSkillWeightageMap();
		// Get aggregated score per category
		final Map<String, Double> scorePerCategory = this.getAggregateScorePerCategory(input.getInterviews(),
				input.getSoftSkillFeedbackList().get(0));
		final Map<String, Double> finalWeightage = this.evaluationScoreComputationManager.getApplicableWeightage(
				skillWeightageMap, scorePerCategory.keySet());

		final List<SkillScore> scores = scorePerCategory.keySet().stream()
				.map(
						x -> SkillScore.builder()
								.skillId(x)
								.score((double) scorePerCategory.get(x).intValue())
								.weightage(finalWeightage.get(x))
								.build())
				.collect(Collectors.toList());

		return EvaluationScoreData.builder()
				.skillScores(scores)
				.input(input)
				.build();
	}

	private Map<String, Double> getAggregateScorePerCategory(final List<InterviewData> interviews,
			final FeedbackData softSkillsFeedback) {
		final Map<String, Double> sumOfFeedbackScorePerCategory = new HashMap<>();
		final Map<String, Double> sumOfFeedbackWeightsPerCategory = new HashMap<>();
		final List<QuestionData> questions = this.populateFlagsForScoreCalculation(interviews).stream()
				.map(InterviewData::getQuestions)
				.flatMap(Collection::stream).collect(Collectors.toList());
		for (final QuestionData question : questions) {
			if (this.isQuestionToBeConsidered(question)) {
				for (final FeedbackData feedback : question.getFeedbacks()) {
					this.updateFeedbackScoreAndWeightsPerCategory(
							feedback,
							question.getType(),
							sumOfFeedbackScorePerCategory,
							sumOfFeedbackWeightsPerCategory, question.getTimeSpentFlag());
				}
			}
		}
		softSkillsFeedback.setLengthFlag(EvaluationStrategy_V10Constants.FeedbackLengthFlag.MID.getValue());
		this.updateFeedbackScoreAndWeightsPerCategory(
				softSkillsFeedback,
				EvaluationStrategy_V9Constants.QuestionType.REQUIRED.getValue(),
				sumOfFeedbackScorePerCategory,
				sumOfFeedbackWeightsPerCategory, EvaluationStrategy_V10Constants.QuestionTimeSpentFlag.MID.getValue());

		final Map<String, Double> weightedAverageCategoryLevel = new HashMap<>();
		sumOfFeedbackScorePerCategory.forEach(
				(x, y) -> {
					weightedAverageCategoryLevel.put(x, y / sumOfFeedbackWeightsPerCategory.get(x));
				});
		return weightedAverageCategoryLevel;
	}

	private Float getFeedbackScore(final FeedbackData feedback) {
		final Float normalisedRating = this.feedbackNormalisationUtil.getCappedNormalisedRating(feedback,
				NormalisationVersionFetcher.getNormalisationAlgoVersion(SCORING_ALGO_VERSION));
		final EvaluationStrategy_V10Constants.FeedbackDifficulty difficulty = EvaluationStrategy_V10Constants.FeedbackDifficulty
				.fromString(
						feedback.getDifficulty());
		final Integer scoreFactor;
		switch (difficulty) {
			case EASY:
				scoreFactor = normalisedRating > 5 ? 40 : 75;
				break;
			case MODERATE:
				scoreFactor = 60;
				break;
			case HARD:
				scoreFactor = normalisedRating > 5 ? 70 : 50;
				break;
			default:
				throw new IllegalArgumentException("Invalid Difficulty : " + difficulty.getValue());
		}
		return 450 + (normalisedRating - 5) * scoreFactor;
	}

	private Double getFeedbackWeightage(final FeedbackData feedback, final String type,
			final String timeSpentInQuestionFlag) {
		final Float normalisedRating = this.feedbackNormalisationUtil.getCappedNormalisedRating(feedback,
				NormalisationVersionFetcher.getNormalisationAlgoVersion(SCORING_ALGO_VERSION));
		final Double modifiedWeightageOfQuestion = QuestionTypeWeightageCalculator_V10.getModifiedWeightage(
				EvaluationStrategy_V10Constants.QuestionType.fromString(type), normalisedRating);
		final Double modifiedWeightageOfFeedback = FeedbackWeightageCalculator_V10.getModifiedWeightage(
				EvaluationStrategy_V10Constants.FeedbackDifficulty.fromString(feedback.getDifficulty()),
				normalisedRating);
		final Double modifiedWeightageOfQuestionTimeAndFeedbackLength = ModifiedWeightageCalculator_V10
				.getModifiedWeightageForFeedbackLengthAndQuestionTime(
						EvaluationStrategy_V10Constants.FeedbackLengthFlag.fromString(feedback.getLengthFlag()),
						EvaluationStrategy_V10Constants.QuestionTimeSpentFlag.fromString(timeSpentInQuestionFlag));
		return modifiedWeightageOfQuestion * modifiedWeightageOfFeedback
				* modifiedWeightageOfQuestionTimeAndFeedbackLength;
	}

	private boolean isQuestionToBeConsidered(final QuestionData question) {
		return !(EvaluationStrategy_V10Constants.QuestionType.DELETED
				.getValue()
				.equals(question.getType())
				|| EvaluationStrategy_V10Constants.QuestionType.NON_EVALUATIVE
						.getValue()
						.equals(question.getType()));
	}

	private void updateFeedbackScoreAndWeightsPerCategory(
			final FeedbackData feedbackData,
			final String questionType,
			final Map<String, Double> sumOfFeedbackScorePerCategory,
			final Map<String, Double> sumOfFeedbackWeightsPerCategory, final String timeSpentInQuestionFlag) {
		if (feedbackData.getRating() != 0) {
			Double categoryScore = sumOfFeedbackScorePerCategory.getOrDefault(feedbackData.getCategoryId(), 0D);
			final Double categoryWeights = sumOfFeedbackWeightsPerCategory.getOrDefault(feedbackData.getCategoryId(),
					0D);
			final Double feedbackWeightage = this.getFeedbackWeightage(feedbackData, questionType,
					timeSpentInQuestionFlag);
			categoryScore += this.getFeedbackScore(feedbackData) * feedbackWeightage;
			sumOfFeedbackScorePerCategory.put(feedbackData.getCategoryId(), categoryScore);
			sumOfFeedbackWeightsPerCategory.put(
					feedbackData.getCategoryId(), categoryWeights + feedbackWeightage);
		}
	}

	private List<InterviewData> populateFlagsForScoreCalculation(final List<InterviewData> interviews) {
		for (int j = 0; j < interviews.size(); j++) {
			InterviewData interview = interviews.get(j);
			final List<QuestionData> existingQuestions = interview.getQuestions();
			existingQuestions.addAll(interview.getQuestions().stream().map(QuestionData::getFollowUpQuestions)
					.filter(Objects::nonNull).flatMap(Collection::stream).collect(Collectors.toList()));
			final List<QuestionData> questions = this.feedbackLengthFlagCalculator_v10.calculate(
					interview.getQuestions().stream().filter(this::isQuestionToBeConsidered)
							.collect(Collectors.toList()));
			questions.addAll(existingQuestions.stream()
					.filter(x -> questions.stream().noneMatch(y -> y.getId().equals(x.getId())))
					.collect(Collectors.toList()));
			questions.sort(Comparator.comparing(QuestionData::getSerialNumber));
			interview = interview.toBuilder()
					.questions(this.populateTimeSpentFlags(questions, interview.getLastQuestionEnd()))
					.build();
			interviews.set(j, interview);
		}
		return interviews;
	}

	private List<QuestionData> populateTimeSpentFlags(final List<QuestionData> questions,
			final Long lastQuestionEndTime) {
		final List<QuestionData> nonDeletedQuestions = this.filterDeletedQuestions(questions);
		for (int i = 0; i < nonDeletedQuestions.size() - 1; i++) {
			final String timeSpentFlag = QuestionTimeSpentFlagCalculator_V10
					.calculate(nonDeletedQuestions.get(i).getStartTimeEpoch(),
							nonDeletedQuestions.get(i + 1).getStartTimeEpoch())
					.getValue();
			final QuestionData questionData = nonDeletedQuestions.get(i).toBuilder().timeSpentFlag(timeSpentFlag)
					.build();
			nonDeletedQuestions.set(i, questionData);
		}
		nonDeletedQuestions.set(nonDeletedQuestions.size() - 1,
				nonDeletedQuestions.get(nonDeletedQuestions.size() - 1).toBuilder()
						.timeSpentFlag(QuestionTimeSpentFlagCalculator_V10
								.calculate(nonDeletedQuestions.get(nonDeletedQuestions.size() - 1).getStartTimeEpoch(),
										lastQuestionEndTime)
								.getValue())
						.build());
		return nonDeletedQuestions;
	}

	private List<QuestionData> filterDeletedQuestions(final List<QuestionData> questions) {
		return questions.stream().filter(x -> !EvaluationStrategy_V10Constants.QuestionType.DELETED.getValue()
				.equals(x.getType())).collect(Collectors.toList());
	}
}
