/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation;

import static org.junit.Assert.*;

import com.barraiser.common.graphql.types.SkillScore;
import com.barraiser.onboarding.interview.pojo.FeedbackData;
import com.barraiser.onboarding.interview.pojo.QuestionData;
import com.barraiser.onboarding.interview.evaluation.scores.pojo.ComputeEvaluationScoresData;
import com.barraiser.onboarding.interview.evaluation.scores.EvaluationStrategy_V7;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class EvaluationStrategy_V7Test {

	@InjectMocks
	private EvaluationStrategy_V7 evaluationStrategy_v7;

	@Mock
	private EvaluationScoreComputationManager evaluationScoreComputationManager;

	@Test
	public void testEvaluationStrategy() throws Exception {
		final String evaluationId = "test_evaluation_id";

		final Map<String, Double> skillWeightageMap = new HashMap<String, Double>();
		skillWeightageMap.put("1", 400.0);
		skillWeightageMap.put("2", 350.0);
		skillWeightageMap.put("3", 250.0);
		skillWeightageMap.put("52", 100.0);

		int i = 0;
		final List<QuestionData> listOfQuestions = List.of(
				QuestionData.builder()
						.id(UUID.randomUUID().toString())
						.serialNumber(i++)
						.question(UUID.randomUUID().toString())
						.startTimeEpoch(1607677320l)
						.type("REQUIRED")
						.feedbacks(
								List.of(
										FeedbackData.builder()
												.id(UUID.randomUUID().toString())
												.categoryId("1")
												.rating(6.0F)
												.normalisedRating(6.5F)
												.difficulty("EASY")
												.feedback(UUID.randomUUID().toString())
												.build()))
						.build(),
				QuestionData.builder()
						.id(UUID.randomUUID().toString())
						.serialNumber(i++)
						.question(UUID.randomUUID().toString())
						.type("REQUIRED")
						.startTimeEpoch(1607677800l)
						.feedbacks(
								List.of(
										FeedbackData.builder()
												.id(UUID.randomUUID().toString())
												.categoryId("3")
												.rating(3.0F)
												.normalisedRating(2.9F)
												.difficulty("MODERATE")
												.feedback(UUID.randomUUID().toString())
												.build()))
						.build(),
				QuestionData.builder()
						.id(UUID.randomUUID().toString())
						.serialNumber(i++)
						.question(UUID.randomUUID().toString())
						.type("GOOD_TO_KNOW")
						.startTimeEpoch(1607678040l)
						.feedbacks(
								List.of(
										FeedbackData.builder()
												.id(UUID.randomUUID().toString())
												.categoryId("2")
												.rating(9.0F)
												.normalisedRating(8.8F)
												.difficulty("HARD")
												.feedback(UUID.randomUUID().toString())
												.build()))
						.build(),

				// irrelevant question
				QuestionData.builder()
						.id(UUID.randomUUID().toString())
						.serialNumber(i++)
						.question(UUID.randomUUID().toString())
						.startTimeEpoch(1607678820l)
						.type("DELETED")
						.feedbacks(
								List.of(
										FeedbackData.builder()
												.id(UUID.randomUUID().toString())
												.categoryId("1")
												.rating(1.0F)
												.normalisedRating(1.2F)
												.difficulty("")
												.feedback(UUID.randomUUID().toString())
												.build()))
						.build(),
				QuestionData.builder()
						.id(UUID.randomUUID().toString())
						.serialNumber(i++)
						.question(UUID.randomUUID().toString())
						.type("NON_EVALUATIVE")
						.startTimeEpoch(1607680020l)
						.feedbacks(
								List.of(
										FeedbackData.builder()
												.id(UUID.randomUUID().toString())
												.categoryId("52")
												.feedback(UUID.randomUUID().toString())
												.build()))
						.build(),
				QuestionData.builder()
						.id(UUID.randomUUID().toString())
						.serialNumber(i++)
						.question(UUID.randomUUID().toString())
						.type("GOOD_TO_KNOW")
						.startTimeEpoch(1607680020l)
						.feedbacks(
								List.of(
										FeedbackData.builder()
												.id(UUID.randomUUID().toString())
												.categoryId("52")
												.rating(4.0F)
												.normalisedRating(6.0F)
												.difficulty("HARD")
												.feedback(UUID.randomUUID().toString())
												.build()))
						.build(),
				QuestionData.builder()
						.id(UUID.randomUUID().toString())
						.serialNumber(i++)
						.question(UUID.randomUUID().toString())
						.type("REQUIRED")
						.startTimeEpoch(1607680020l)
						.feedbacks(
								List.of(
										FeedbackData.builder()
												.id(UUID.randomUUID().toString())
												.categoryId("52")
												.rating(5.0F)
												.normalisedRating(5.0F)
												.difficulty("HARD")
												.feedback(UUID.randomUUID().toString())
												.build()))
						.build(),
				QuestionData.builder()
						.id(UUID.randomUUID().toString())
						.serialNumber(i++)
						.question(UUID.randomUUID().toString())
						.type("REQUIRED")
						.startTimeEpoch(1607680020l)
						.feedbacks(
								List.of(
										FeedbackData.builder()
												.id(UUID.randomUUID().toString())
												.categoryId("1")
												.rating(8.0F)
												.normalisedRating(9.9F)
												.difficulty("MODERATE")
												.feedback(UUID.randomUUID().toString())
												.build()))
						.build());

		final ComputeEvaluationScoresData input = ComputeEvaluationScoresData.builder()
				.questions(listOfQuestions)
				.skillWeightageMap(skillWeightageMap)
				.build();

		final List<SkillScore> evaluationScoreList = this.evaluationStrategy_v7.computeEvaluationScore(input)
				.getSkillScores();

		for (final SkillScore evaluationScore : evaluationScoreList) {
			if ("1".equalsIgnoreCase(evaluationScore.getSkillId())) {
				assertEquals(666D, evaluationScore.getScore().doubleValue(), 0.0);
			}
			if ("2".equalsIgnoreCase(evaluationScore.getSkillId())) {
				assertEquals(716D, evaluationScore.getScore().doubleValue(), 0.0);
			}
			if ("3".equalsIgnoreCase(evaluationScore.getSkillId())) {
				assertEquals(324D, evaluationScore.getScore().doubleValue(), 0.0);
			}
			if ("52".equalsIgnoreCase(evaluationScore.getSkillId())) {
				assertEquals(480D, evaluationScore.getScore().doubleValue(), 0.0);
			}
		}
	}
}
