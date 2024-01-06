/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores;

import com.barraiser.common.graphql.types.SkillScore;
import com.barraiser.onboarding.common.TestingUtil;
import com.barraiser.onboarding.interview.evaluation.EvaluationScoreComputationManager;
import com.barraiser.onboarding.interview.evaluation.scores.pojo.ComputeEvaluationScoresData;
import com.barraiser.onboarding.interview.evaluation.scores.v10.EvaluationStrategy_V10;
import com.barraiser.onboarding.interview.evaluation.scores.v10.FeedbackLengthFlagCalculator_V10;
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
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class EvaluationStrategy_V10Test {
	@InjectMocks
	private EvaluationStrategy_V10 evaluationStrategy_v10;

	@Spy
	private FeedbackLengthFlagCalculator_V10 feedbackLengthFlagCalculator_v10;

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
				"src/test/resources/json_data_files/EvaluationStrategy_V10TestDataJson.json",
				EvaluationStrategyTestData.class);

		final ComputeEvaluationScoresData data = ComputeEvaluationScoresData.builder()
				.interviews(testData.getInterviews())
				.softSkillFeedbackList(testData.getSoftSkillFeedback())
				.skillWeightageMap(testData.getSkillWeightageMap())
				.build();

		final List<SkillScore> skillScores = this.evaluationStrategy_v10.computeEvaluationScore(data)
				.getSkillScores();

		for (final SkillScore evaluationScore : skillScores) {
			if ("7".equalsIgnoreCase(evaluationScore.getSkillId())) {
				assertEquals(623D, evaluationScore.getScore().doubleValue(), 0.0);
			}
			if ("8".equalsIgnoreCase(evaluationScore.getSkillId())) {
				assertEquals(511D, evaluationScore.getScore().doubleValue(), 0.0);
			}
			if ("9".equalsIgnoreCase(evaluationScore.getSkillId())) {
				assertEquals(564D, evaluationScore.getScore().doubleValue(), 0.0);
			}
			if ("52".equalsIgnoreCase(evaluationScore.getSkillId())) {
				assertEquals(600D, evaluationScore.getScore().doubleValue(), 0.0);
			}
		}
	}
}
