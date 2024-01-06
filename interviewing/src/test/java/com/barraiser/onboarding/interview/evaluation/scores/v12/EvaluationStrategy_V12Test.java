/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores.v12;

import com.barraiser.common.graphql.types.SkillScore;
import com.barraiser.onboarding.common.Constants;
import com.barraiser.onboarding.common.TestingUtil;
import com.barraiser.onboarding.interview.evaluation.EvaluationScoreComputationManager;
import com.barraiser.onboarding.interview.evaluation.scores.EvaluationStrategyTestData;
import com.barraiser.onboarding.interview.evaluation.scores.pojo.ComputeEvaluationScoresData;
import com.barraiser.onboarding.interview.feeback.FeedbackNormalisationUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class EvaluationStrategy_V12Test {

	@InjectMocks
	private EvaluationStrategy_V12 evaluationStrategy_v12;

	@Spy
	private FeedbackLengthFlagCalculator_V12 feedbackLengthFlagCalculator_v12;

	@InjectMocks
	private TestingUtil testingUtil;

	@Spy
	private ObjectMapper objectMapper;

	@Mock
	private EvaluationScoreComputationManager evaluationScoreComputationManager;

	@Spy
	private FeedbackNormalisationUtil feedbackNormalisationUtil;

	@Test
	public void testEvaluationStrategy() throws IOException {
		final EvaluationStrategyTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/EvaluationStrategy_V12TestDataJson.json",
				EvaluationStrategyTestData.class);

		final ComputeEvaluationScoresData data = ComputeEvaluationScoresData.builder()
				.interviews(testData.getInterviews())
				.softSkillFeedbackList(testData.getSoftSkillFeedback())
				.skillWeightageMap(testData.getSkillWeightageMap())
				.build();

		final List<SkillScore> skillScores = this.evaluationStrategy_v12.computeEvaluationScore(data)
				.getSkillScores();

		for (final SkillScore evaluationScore : skillScores) {
			if ("7".equalsIgnoreCase(evaluationScore.getSkillId())) {
				assertEquals(541D, evaluationScore.getScore().doubleValue(), 0.0);
			}
			if ("8".equalsIgnoreCase(evaluationScore.getSkillId())) {
				assertEquals(515D, evaluationScore.getScore().doubleValue(), 0.0);
			}
			if ("52".equalsIgnoreCase(evaluationScore.getSkillId())) {
				assertEquals(600D, evaluationScore.getScore().doubleValue(), 0.0);
			}
		}
	}

	@Test
	public void testEvaluationStrategyWithOthersCategoryConfigured() throws IOException {
		final EvaluationStrategyTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/EvaluationStrategy_V12TestDataJsonWithSkillWeightageForOthersCategory.json",
				EvaluationStrategyTestData.class);

		final ComputeEvaluationScoresData data = ComputeEvaluationScoresData.builder()
				.interviews(testData.getInterviews())
				.softSkillFeedbackList(testData.getSoftSkillFeedback())
				.skillWeightageMap(testData.getSkillWeightageMap())
				.build();

		final List<SkillScore> skillScores = this.evaluationStrategy_v12.computeEvaluationScore(data)
				.getSkillScores();

		for (final SkillScore evaluationScore : skillScores) {
			if ("7".equalsIgnoreCase(evaluationScore.getSkillId())) {
				assertEquals(541D, evaluationScore.getScore().doubleValue(), 0.0);
			}
			if ("8".equalsIgnoreCase(evaluationScore.getSkillId())) {
				assertEquals(515D, evaluationScore.getScore().doubleValue(), 0.0);
			}
			if ("52".equalsIgnoreCase(evaluationScore.getSkillId())) {
				assertEquals(600D, evaluationScore.getScore().doubleValue(), 0.0);
			}
		}
	}

	@Test
	public void testEvaluationStrategyWithOnlyFeedbackForOthersCategory() throws IOException {
		final EvaluationStrategyTestData testData = this.testingUtil.getTestingData(
				"src/test/resources/json_data_files/EvaluationStrategy_V12TestDataJsonWithOnlyFeedbackForOthersCategory.json",
				EvaluationStrategyTestData.class);

		final ComputeEvaluationScoresData data = ComputeEvaluationScoresData.builder()
				.interviews(testData.getInterviews())
				.softSkillFeedbackList(testData.getSoftSkillFeedback())
				.skillWeightageMap(testData.getSkillWeightageMap())
				.build();

		final List<SkillScore> skillScores = this.evaluationStrategy_v12.computeEvaluationScore(data)
				.getSkillScores();

		assertEquals(2, skillScores.size());

		for (final SkillScore evaluationScore : skillScores) {
			if (Constants.OTHERS_SKILL_ID.equalsIgnoreCase(evaluationScore.getSkillId())) {
				assertEquals(571D, evaluationScore.getScore().doubleValue(), 0.0);
			}
			if ("52".equalsIgnoreCase(evaluationScore.getSkillId())) {
				assertEquals(600D, evaluationScore.getScore().doubleValue(), 0.0);
			}
		}
	}

}
