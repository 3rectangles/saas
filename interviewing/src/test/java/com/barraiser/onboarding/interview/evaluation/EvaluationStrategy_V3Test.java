/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import com.barraiser.common.graphql.types.SkillScore;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.pojo.FeedbackData;
import com.barraiser.onboarding.interview.pojo.QuestionData;
import com.barraiser.onboarding.interview.evaluation.scores.pojo.ComputeEvaluationScoresData;
import com.barraiser.onboarding.interview.evaluation.scores.EvaluationStrategy_V3;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RunWith(MockitoJUnitRunner.class)
public class EvaluationStrategy_V3Test {

	@Mock
	private ModifiedWeightageRepository modifiedWeightageRepository;

	@Spy
	private ObjectMapper objectMapper;

	@InjectMocks
	private EvaluationStrategy_V3 evaluationStrategy_v3;

	@Test
	public void testEvaluationStrategy() throws Exception {
		final String evaluationId = "test_evaluation_id";

		final Map<String, Double> skillWeightageMap = new HashMap<String, Double>();
		skillWeightageMap.put("1", 400.0);
		skillWeightageMap.put("2", 350.0);
		skillWeightageMap.put("3", 250.0);

		int i = 0;
		final List<QuestionData> listOfQuestions = List.of(
				QuestionData.builder()
						.id(UUID.randomUUID().toString())
						.serialNumber(i++)
						.question(UUID.randomUUID().toString())
						.startTimeEpoch(1607677320l)
						.feedbacks(
								List.of(
										FeedbackData.builder()
												.id(UUID.randomUUID().toString())
												.categoryId("1")
												.rating(7.0F)
												.difficulty("EASY")
												.feedback(UUID.randomUUID().toString())
												.build()))
						.build(),
				QuestionData.builder()
						.id(UUID.randomUUID().toString())
						.serialNumber(i++)
						.question(UUID.randomUUID().toString())
						.startTimeEpoch(1607677800l)
						.feedbacks(
								List.of(
										FeedbackData.builder()
												.id(UUID.randomUUID().toString())
												.categoryId("3")
												.rating(3.0F)
												.difficulty("MODERATE")
												.feedback(UUID.randomUUID().toString())
												.build()))
						.build(),
				QuestionData.builder()
						.id(UUID.randomUUID().toString())
						.serialNumber(i++)
						.question(UUID.randomUUID().toString())
						.startTimeEpoch(1607678040l)
						.feedbacks(
								List.of(
										FeedbackData.builder()
												.id(UUID.randomUUID().toString())
												.categoryId("1")
												.rating(7.0F)
												.difficulty("VERY_EASY")
												.feedback(UUID.randomUUID().toString())
												.build(),
										FeedbackData.builder()
												.id(UUID.randomUUID().toString())
												.categoryId("2")
												.rating(9.0F)
												.difficulty("HARD")
												.feedback(UUID.randomUUID().toString())
												.build(),
										FeedbackData.builder()
												.id(UUID.randomUUID().toString())
												.categoryId("3")
												.rating(1.0F)
												.difficulty("VERY_HARD")
												.feedback(UUID.randomUUID().toString())
												.build()))
						.build(),

				// irrelevant question
				QuestionData.builder()
						.id(UUID.randomUUID().toString())
						.serialNumber(i++)
						.question(UUID.randomUUID().toString())
						.startTimeEpoch(1607678820l)
						.irrelevant(true)
						.feedbacks(
								List.of(
										FeedbackData.builder()
												.id(UUID.randomUUID().toString())
												.categoryId("1")
												.rating(1.0F)
												.difficulty("")
												.feedback(UUID.randomUUID().toString())
												.build()))
						.build(),
				QuestionData.builder()
						.id(UUID.randomUUID().toString())
						.serialNumber(i++)
						.question(UUID.randomUUID().toString())
						.startTimeEpoch(1607680020l)
						.feedbacks(
								List.of(
										FeedbackData.builder()
												.id(UUID.randomUUID().toString())
												.categoryId("2")
												.rating(8.0F)
												.difficulty("N.A")
												.feedback(UUID.randomUUID().toString())
												.build(),
										FeedbackData.builder()
												.id(UUID.randomUUID().toString())
												.categoryId("3")
												.rating(7.0F)
												.difficulty("VERY_HARD")
												.feedback(UUID.randomUUID().toString())
												.build()))
						.build());

		final ComputeEvaluationScoresData input = ComputeEvaluationScoresData.builder()
				.questions(listOfQuestions)
				.skillWeightageMap(skillWeightageMap)
				.build();

		// No need to save the test scores
		final List<ModifiedWeightageDAO> listOfModifiedWeightages = List.of(
				ModifiedWeightageDAO.builder()
						.difficulty("VERY_EASY")
						.rating(1.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("VERY_EASY")
						.rating(2.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("VERY_EASY")
						.rating(3.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("VERY_EASY")
						.rating(4.0D)
						.weightage(3.5D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("VERY_EASY")
						.rating(5.0D)
						.weightage(3.5D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("VERY_EASY")
						.rating(6.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("VERY_EASY")
						.rating(7.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("VERY_EASY")
						.rating(8.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("VERY_EASY")
						.rating(9.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("VERY_EASY")
						.rating(10.0D)
						.weightage(1.0D)
						.build(),
				// ------------------------------
				ModifiedWeightageDAO.builder()
						.difficulty("EASY")
						.rating(1.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("EASY")
						.rating(2.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("EASY")
						.rating(3.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("EASY")
						.rating(4.0D)
						.weightage(2.5D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("EASY")
						.rating(5.0D)
						.weightage(2.5D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("EASY")
						.rating(6.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("EASY")
						.rating(7.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("EASY")
						.rating(8.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("EASY")
						.rating(9.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("EASY")
						.rating(10.0D)
						.weightage(1.0D)
						.build(),
				// ------------------------------
				ModifiedWeightageDAO.builder()
						.difficulty("MODERATE")
						.rating(1.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("MODERATE")
						.rating(2.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("MODERATE")
						.rating(3.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("MODERATE")
						.rating(4.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("MODERATE")
						.rating(5.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("MODERATE")
						.rating(6.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("MODERATE")
						.rating(7.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("MODERATE")
						.rating(8.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("MODERATE")
						.rating(9.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("MODERATE")
						.rating(10.0D)
						.weightage(1.0D)
						.build(),
				// ------------------------------
				ModifiedWeightageDAO.builder()
						.difficulty("HARD")
						.rating(1.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("HARD")
						.rating(2.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("HARD")
						.rating(3.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("HARD")
						.rating(4.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("HARD")
						.rating(5.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("HARD")
						.rating(6.0D)
						.weightage(2.5D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("HARD")
						.rating(7.0D)
						.weightage(2.5D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("HARD")
						.rating(8.0D)
						.weightage(2.5D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("HARD")
						.rating(9.0D)
						.weightage(3.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("HARD")
						.rating(10.0D)
						.weightage(3.0D)
						.build(),
				// ------------------------------
				ModifiedWeightageDAO.builder()
						.difficulty("VERY_HARD")
						.rating(1.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("VERY_HARD")
						.rating(2.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("VERY_HARD")
						.rating(3.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("VERY_HARD")
						.rating(4.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("VERY_HARD")
						.rating(5.0D)
						.weightage(1.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("VERY_HARD")
						.rating(6.0D)
						.weightage(3.5D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("VERY_HARD")
						.rating(7.0D)
						.weightage(3.5D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("VERY_HARD")
						.rating(8.0D)
						.weightage(3.5D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("VERY_HARD")
						.rating(9.0D)
						.weightage(4.0D)
						.build(),
				ModifiedWeightageDAO.builder()
						.difficulty("VERY_HARD")
						.rating(10.0D)
						.weightage(4.0D)
						.build()

		// -----------------------------

		);
		when(this.modifiedWeightageRepository.findAll()).thenReturn(listOfModifiedWeightages);

		final List<SkillScore> evaluationScores = this.evaluationStrategy_v3.computeEvaluationScore(input)
				.getSkillScores();

		for (final SkillScore evaluationScore : evaluationScores) {
			if ("1".equalsIgnoreCase(evaluationScore.getSkillId())) {
				assertEquals(467D, evaluationScore.getScore().doubleValue(), 0.0);
			}
			if ("2".equalsIgnoreCase(evaluationScore.getSkillId())) {
				assertEquals(661D, evaluationScore.getScore().doubleValue(), 0.0);
			}
			if ("3".equalsIgnoreCase(evaluationScore.getSkillId())) {
				assertEquals(582D, evaluationScore.getScore().doubleValue(), 0.0);
			}
		}
	}
}
