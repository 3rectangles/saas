/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation;

import com.barraiser.onboarding.common.Constants;
import com.barraiser.onboarding.common.TestingUtil;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.interview.evaluation.scores.pojo.ComputeEvaluationScoresData;
import com.barraiser.onboarding.interview.feeback.FeedbackNormalisationUtil;
import com.barraiser.onboarding.interview.jobrole.SkillWeightageManager;
import com.barraiser.onboarding.interview.pojo.FeedbackData;
import com.barraiser.onboarding.interview.pojo.InterviewData;
import com.barraiser.onboarding.interview.pojo.QuestionData;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EvaluationGenerationRequestToDTOConverterTest {

	@Mock
	private QuestionRepository questionRepository;
	@Mock
	private SkillWeightageManager skillWeightageManager;
	@Mock
	private ObjectMapper objectMapper;
	@Mock
	private FeedbackRepository feedbackRepository;
	@Mock
	private FeedbackNormalisationUtil feedbackNormalisationUtil;
	@InjectMocks
	private EvaluationGenerationRequestToDTOConverter evaluationGenerationRequestToDTOConverter;
	@InjectMocks
	private TestingUtil testingUtil;

	@Test
	public void generateDTOForCalculatingEvaluationScores() throws IOException {
		final List<InterviewData> interviews = List.of(
				InterviewData.builder().id("i1").rescheduleCount(0).build(),
				InterviewData.builder().id("i2").rescheduleCount(2).build());

		when(this.questionRepository.findAllByInterviewIdAndRescheduleCountOrderByStartTimeEpochAsc("i1", 0))
				.thenReturn(List.of(
						QuestionDAO.builder().id("q1").interviewId("i1").build(),
						QuestionDAO.builder().id("q2").interviewId("i1").build()));

		when(this.questionRepository.findAllByInterviewIdAndRescheduleCountOrderByStartTimeEpochAsc("i2", 2))
				.thenReturn(List.of(
						QuestionDAO.builder().id("q3").interviewId("i2").build()));

		when(this.feedbackRepository.findByReferenceIdIn(List.of("q1", "q2", "q3")))
				.thenReturn(List.of(
						FeedbackDAO.builder().id("f1").referenceId("q1").rating(1F).normalisedRatingMappings(
								List.of(
										NormalisedRatingMapping.builder().normalisationVersion("1")
												.cappedNormalisedRating(1.7F).build(),
										NormalisedRatingMapping.builder().normalisationVersion("2")
												.cappedNormalisedRating(2.8F).build()))
								.build(),
						FeedbackDAO.builder().id("f2").referenceId("q1").rating(8F).normalisedRatingMappings(
								List.of(
										NormalisedRatingMapping.builder().normalisationVersion("1")
												.cappedNormalisedRating(5.7F).build(),
										NormalisedRatingMapping.builder().normalisationVersion("2")
												.cappedNormalisedRating(5.8F).build()))
								.build(),
						FeedbackDAO.builder().id("f3").referenceId("q2").rating(9F).normalisedRatingMappings(
								List.of(
										NormalisedRatingMapping.builder().normalisationVersion("1")
												.cappedNormalisedRating(8.7F).build(),
										NormalisedRatingMapping.builder().normalisationVersion("2")
												.cappedNormalisedRating(9.8F).build()))
								.build(),
						FeedbackDAO.builder().id("f4").referenceId("q3").rating(2F).normalisedRatingMappings(
								List.of(
										NormalisedRatingMapping.builder().normalisationVersion("1")
												.cappedNormalisedRating(2.7F).build(),
										NormalisedRatingMapping.builder().normalisationVersion("2")
												.cappedNormalisedRating(2.8F).build()))
								.build(),
						FeedbackDAO.builder().id("f5").referenceId("q3").rating(4F).normalisedRatingMappings(
								List.of(
										NormalisedRatingMapping.builder().normalisationVersion("1")
												.cappedNormalisedRating(1F).build(),
										NormalisedRatingMapping.builder().normalisationVersion("2")
												.cappedNormalisedRating(9.8F).build()))
								.build()));

		when(this.feedbackRepository.findByReferenceIdIn(List.of("i1", "i2")))
				.thenReturn(
						List.of(
								FeedbackDAO.builder().id("s1").referenceId("i1").rating(1F).normalisedRatingMappings(
										List.of(
												NormalisedRatingMapping.builder().normalisationVersion("1")
														.cappedNormalisedRating(1.7F).build(),
												NormalisedRatingMapping.builder().normalisationVersion("2")
														.cappedNormalisedRating(2.8F).build()))
										.build(),
								FeedbackDAO.builder().id("s2").referenceId("i1").rating(8F).normalisedRatingMappings(
										List.of(
												NormalisedRatingMapping.builder().normalisationVersion("1")
														.cappedNormalisedRating(5.7F).build(),
												NormalisedRatingMapping.builder().normalisationVersion("2")
														.cappedNormalisedRating(5.8F).build()))
										.build(),
								FeedbackDAO.builder().id("s3").referenceId("i2").rating(9F).normalisedRatingMappings(
										List.of(
												NormalisedRatingMapping.builder().normalisationVersion("1")
														.cappedNormalisedRating(8.7F).build(),
												NormalisedRatingMapping.builder().normalisationVersion("2")
														.cappedNormalisedRating(9.8F).build()))
										.build(),
								FeedbackDAO.builder().id("s4").referenceId("i2").rating(2F).normalisedRatingMappings(
										List.of(
												NormalisedRatingMapping.builder().normalisationVersion("1")
														.cappedNormalisedRating(2.7F).build(),
												NormalisedRatingMapping.builder().normalisationVersion("2")
														.cappedNormalisedRating(2.8F).build()))
										.build()));

		when(this.skillWeightageManager.getSkillWeightageForEvaluation("e1")).thenReturn(List.of(
				SkillWeightageDAO.builder().skillId("1").weightage(60D).build(),
				SkillWeightageDAO.builder().skillId("52").weightage(40D).build()));

		final ComputeEvaluationScoresData actualData = this.evaluationGenerationRequestToDTOConverter
				.process(interviews, "e1");
		final ComputeEvaluationScoresData expectedData = ComputeEvaluationScoresData.builder()
				.evaluationId("e1")
				.interviews(List.of(
						InterviewData.builder().id("i1")
								.questions(
										List.of(
												QuestionData.builder().id("q1").interviewId("i1")
														.feedbacks(
																List.of(
																		FeedbackData.builder().id("f1")
																				.referenceId("q1").rating(1F)
																				.normalisedRatingMappings(
																						List.of(
																								NormalisedRatingMapping
																										.builder()
																										.normalisationVersion(
																												"1")
																										.cappedNormalisedRating(
																												1.7F)
																										.build(),
																								NormalisedRatingMapping
																										.builder()
																										.normalisationVersion(
																												"2")
																										.cappedNormalisedRating(
																												2.8F)
																										.build()))
																				.build(),
																		FeedbackData.builder().id("f2")
																				.referenceId("q1").rating(8F)
																				.normalisedRatingMappings(
																						List.of(
																								NormalisedRatingMapping
																										.builder()
																										.normalisationVersion(
																												"1")
																										.cappedNormalisedRating(
																												5.7F)
																										.build(),
																								NormalisedRatingMapping
																										.builder()
																										.normalisationVersion(
																												"2")
																										.cappedNormalisedRating(
																												5.8F)
																										.build()))
																				.build()))
														.build(),
												QuestionData.builder().id("q2").interviewId("i1")
														.feedbacks(List.of(
																FeedbackData.builder().id("f3").referenceId("q2")
																		.rating(9F).normalisedRatingMappings(
																				List.of(
																						NormalisedRatingMapping
																								.builder()
																								.normalisationVersion(
																										"1")
																								.cappedNormalisedRating(
																										8.7F)
																								.build(),
																						NormalisedRatingMapping
																								.builder()
																								.normalisationVersion(
																										"2")
																								.cappedNormalisedRating(
																										9.8F)
																								.build()))
																		.build()))
														.build()

										))
								.build(),
						InterviewData.builder().id("i2")
								.questions(
										List.of(
												QuestionData.builder().id("q3").interviewId("i2")
														.feedbacks(List.of(
																FeedbackData.builder().id("f4").referenceId("q3")
																		.rating(2F).normalisedRatingMappings(
																				List.of(
																						NormalisedRatingMapping
																								.builder()
																								.normalisationVersion(
																										"1")
																								.cappedNormalisedRating(
																										2.7F)
																								.build(),
																						NormalisedRatingMapping
																								.builder()
																								.normalisationVersion(
																										"2")
																								.cappedNormalisedRating(
																										2.8F)
																								.build()))
																		.build(),
																FeedbackData.builder().id("f5").referenceId("q3")
																		.rating(4F).normalisedRatingMappings(
																				List.of(
																						NormalisedRatingMapping
																								.builder()
																								.normalisationVersion(
																										"1")
																								.cappedNormalisedRating(
																										1F)
																								.build(),
																						NormalisedRatingMapping
																								.builder()
																								.normalisationVersion(
																										"2")
																								.cappedNormalisedRating(
																										9.8F)
																								.build()))
																		.build()))
														.build()))
								.build()))
				.softSkillFeedbackList(Arrays.asList(FeedbackData.builder().rating(10F).normalisedRatingMappings(
						List.of(
								NormalisedRatingMapping.builder().normalisationVersion("1").cappedNormalisedRating(1.3F)
										.build(),
								NormalisedRatingMapping.builder().normalisationVersion("2").cappedNormalisedRating(1.3F)
										.build()))
						.build()))
				.skillWeightageMap(Map.of("1", 60D, "52", 40D))
				.build();
		assertEquals(expectedData.getEvaluationId(), actualData.getEvaluationId());
		expectedData.getInterviews().forEach(x -> {
			final List<QuestionData> actualQuestions = actualData.getInterviews().stream()
					.filter(y -> y.getId().equals(x.getId())).findFirst().get().getQuestions();
			x.getQuestions().forEach(
					z -> {
						final QuestionData actualQuestion = actualQuestions.stream()
								.filter(u -> u.getId().equals(z.getId())).findFirst().get();
						assertEquals(z.getInterviewId(), actualQuestion.getInterviewId());
						final List<FeedbackData> actualFeedbacks = actualQuestion.getFeedbacks();
						z.getFeedbacks().forEach(
								m -> {
									final FeedbackData actualFeedback = actualFeedbacks.stream()
											.filter(n -> n.getId().equals(m.getId())).findFirst().get();
									assertEquals(m.getRating(), actualFeedback.getRating());
									assertEquals(m.getReferenceId(), actualFeedback.getReferenceId());
									assertEquals(m.getNormalisedRatingMappings(),
											actualFeedback.getNormalisedRatingMappings());
								});
					});
		});
		assertEquals(expectedData.getSoftSkillFeedbackList().get(0).getRating(),
				actualData.getSoftSkillFeedbackList().get(0).getRating());
		assertEquals(expectedData.getSoftSkillFeedbackList().get(0).getNormalisedRatingMappings(),
				actualData.getSoftSkillFeedbackList().get(0).getNormalisedRatingMappings());

	}

	/**
	 * All feedback for others category only
	 */
	@Test
	public void testScenario1() {
		final String evaluationId = "e1";

		final List<QuestionData> questions = List.of(
				QuestionData.builder()
						.feedbacks(
								List.of(
										FeedbackData.builder().categoryId(Constants.OTHERS_SKILL_ID).build(),
										FeedbackData.builder().categoryId(Constants.OTHERS_SKILL_ID).build(),
										FeedbackData.builder().categoryId(Constants.OTHERS_SKILL_ID).build()))
						.build(),
				QuestionData.builder()
						.feedbacks(
								List.of(
										FeedbackData.builder().categoryId(Constants.OTHERS_SKILL_ID).build(),
										FeedbackData.builder().categoryId(Constants.OTHERS_SKILL_ID).build(),
										FeedbackData.builder().categoryId(Constants.OTHERS_SKILL_ID).build()))
						.build());

		when(this.skillWeightageManager.getSkillWeightageForEvaluation(evaluationId))
				.thenReturn(List.of(SkillWeightageDAO.builder()
						.skillId("skill_coding")
						.weightage(45.0)
						.build(),
						SkillWeightageDAO.builder()
								.skillId("skill_LLD")
								.weightage(35.0)
								.build(),
						SkillWeightageDAO.builder()
								.skillId("52")
								.weightage(20.0)
								.build()));

		final Map<String, Double> skillWeightages = this.evaluationGenerationRequestToDTOConverter
				.getSkillWeightages(evaluationId, questions);
		Assert.assertEquals(4, skillWeightages.size());
		Assert.assertEquals((Double) 45.0, skillWeightages.get("skill_coding"));
		Assert.assertEquals((Double) 35.0, skillWeightages.get("skill_LLD"));
		Assert.assertEquals((Double) 20.0, skillWeightages.get("52"));
		Assert.assertEquals((Double) 80.0, skillWeightages.get(Constants.OTHERS_SKILL_ID));
	}

	/**
	 * All feedback for non others and non soft skill
	 */
	@Test
	public void testScenario2() {
		final String evaluationId = "e1";

		final List<QuestionData> questions = List.of(
				QuestionData.builder()
						.feedbacks(
								List.of(
										FeedbackData.builder().categoryId("skill_coding").build(),
										FeedbackData.builder().categoryId("skill_LLD").build(),
										FeedbackData.builder().categoryId("skill_LLD").build()))
						.build(),
				QuestionData.builder()
						.feedbacks(
								List.of(
										FeedbackData.builder().categoryId("skill_coding").build(),
										FeedbackData.builder().categoryId("skill_LLD").build(),
										FeedbackData.builder().categoryId("skill_coding").build()))
						.build());

		when(this.skillWeightageManager.getSkillWeightageForEvaluation(evaluationId))
				.thenReturn(List.of(SkillWeightageDAO.builder()
						.skillId("skill_coding")
						.weightage(45.0)
						.build(),
						SkillWeightageDAO.builder()
								.skillId("skill_LLD")
								.weightage(35.0)
								.build(),
						SkillWeightageDAO.builder()
								.skillId("52")
								.weightage(20.0)
								.build()));

		final Map<String, Double> skillWeightages = this.evaluationGenerationRequestToDTOConverter
				.getSkillWeightages(evaluationId, questions);
		Assert.assertEquals(4, skillWeightages.size());
		Assert.assertEquals((Double) 45.0, skillWeightages.get("skill_coding"));
		Assert.assertEquals((Double) 35.0, skillWeightages.get("skill_LLD"));
		Assert.assertEquals((Double) 20.0, skillWeightages.get("52"));
		Assert.assertEquals((Double) 0.0, skillWeightages.get(Constants.OTHERS_SKILL_ID));
	}

	/**
	 * All feedback for non others and soft skill
	 */
	@Test
	public void testScenario3() {
		final String evaluationId = "e1";

		final List<QuestionData> questions = List.of(
				QuestionData.builder()
						.feedbacks(
								List.of(
										FeedbackData.builder().categoryId("skill_coding").build(),
										FeedbackData.builder().categoryId("skill_LLD").build(),
										FeedbackData.builder().categoryId("52").build()))
						.build(),
				QuestionData.builder()
						.feedbacks(
								List.of(
										FeedbackData.builder().categoryId("skill_coding").build(),
										FeedbackData.builder().categoryId("skill_LLD").build(),
										FeedbackData.builder().categoryId("52").build()))
						.build());

		when(this.skillWeightageManager.getSkillWeightageForEvaluation(evaluationId))
				.thenReturn(List.of(SkillWeightageDAO.builder()
						.skillId("skill_coding")
						.weightage(45.0)
						.build(),
						SkillWeightageDAO.builder()
								.skillId("skill_LLD")
								.weightage(35.0)
								.build(),
						SkillWeightageDAO.builder()
								.skillId("52")
								.weightage(20.0)
								.build()));

		final Map<String, Double> skillWeightages = this.evaluationGenerationRequestToDTOConverter
				.getSkillWeightages(evaluationId, questions);
		Assert.assertEquals(4, skillWeightages.size());
		Assert.assertEquals((Double) 45.0, skillWeightages.get("skill_coding"));
		Assert.assertEquals((Double) 35.0, skillWeightages.get("skill_LLD"));
		Assert.assertEquals((Double) 20.0, skillWeightages.get("52"));
		Assert.assertEquals((Double) 0.0, skillWeightages.get(Constants.OTHERS_SKILL_ID));
	}

	/**
	 * All feedback for soft skill and others
	 * and weightages for other categories as well
	 */
	@Test
	public void testScenario4() {
		final String evaluationId = "e1";

		final List<QuestionData> questions = List.of(
				QuestionData.builder()
						.feedbacks(
								List.of(
										FeedbackData.builder().categoryId(Constants.OTHERS_SKILL_ID).build(),
										FeedbackData.builder().categoryId(Constants.OTHERS_SKILL_ID).build(),
										FeedbackData.builder().categoryId("52").build()))
						.build(),
				QuestionData.builder()
						.feedbacks(
								List.of(
										FeedbackData.builder().categoryId("52").build(),
										FeedbackData.builder().categoryId(Constants.OTHERS_SKILL_ID).build(),
										FeedbackData.builder().categoryId("52").build()))
						.build());

		when(this.skillWeightageManager.getSkillWeightageForEvaluation(evaluationId))
				.thenReturn(List.of(SkillWeightageDAO.builder()
						.skillId("skill_coding")
						.weightage(45.0)
						.build(),
						SkillWeightageDAO.builder()
								.skillId("skill_LLD")
								.weightage(35.0)
								.build(),
						SkillWeightageDAO.builder()
								.skillId("52")
								.weightage(20.0)
								.build()));

		final Map<String, Double> skillWeightages = this.evaluationGenerationRequestToDTOConverter
				.getSkillWeightages(evaluationId, questions);
		Assert.assertEquals(4, skillWeightages.size());
		Assert.assertEquals((Double) 45.0, skillWeightages.get("skill_coding"));
		Assert.assertEquals((Double) 35.0, skillWeightages.get("skill_LLD"));
		Assert.assertEquals((Double) 20.0, skillWeightages.get("52"));
		Assert.assertEquals((Double) 80.0, skillWeightages.get(Constants.OTHERS_SKILL_ID));
	}

	/**
	 * All feedback for soft skill and others
	 * and weightages soft skill categories only
	 */
	@Test
	public void testScenario5() {
		final String evaluationId = "e1";

		final List<QuestionData> questions = List.of(
				QuestionData.builder()
						.feedbacks(
								List.of(
										FeedbackData.builder().categoryId(Constants.OTHERS_SKILL_ID).build(),
										FeedbackData.builder().categoryId(Constants.OTHERS_SKILL_ID).build(),
										FeedbackData.builder().categoryId("52").build()))
						.build(),
				QuestionData.builder()
						.feedbacks(
								List.of(
										FeedbackData.builder().categoryId(Constants.OTHERS_SKILL_ID).build(),
										FeedbackData.builder().categoryId("52").build(),
										FeedbackData.builder().categoryId("52").build()))
						.build());

		when(this.skillWeightageManager.getSkillWeightageForEvaluation(evaluationId))
				.thenReturn(List.of(
						SkillWeightageDAO.builder()
								.skillId("52")
								.weightage(20.0)
								.build()));

		final Map<String, Double> skillWeightages = this.evaluationGenerationRequestToDTOConverter
				.getSkillWeightages(evaluationId, questions);
		Assert.assertEquals(2, skillWeightages.size());
		Assert.assertEquals((Double) 20.0, skillWeightages.get("52"));
		Assert.assertEquals((Double) 80.0, skillWeightages.get(Constants.OTHERS_SKILL_ID));
	}

	/**
	 * All feedback for soft skill and others
	 * and weightages soft skill categories only
	 */
	@Test
	public void testScenario6() {
		final String evaluationId = "e1";

		final List<QuestionData> questions = List.of(
				QuestionData.builder()
						.feedbacks(
								List.of(
										FeedbackData.builder().categoryId("52").build(),
										FeedbackData.builder().categoryId("52").build(),
										FeedbackData.builder().categoryId("52").build()))
						.build(),
				QuestionData.builder()
						.feedbacks(
								List.of(
										FeedbackData.builder().categoryId("52").build(),
										FeedbackData.builder().categoryId("52").build(),
										FeedbackData.builder().categoryId("52").build()))
						.build());

		when(this.skillWeightageManager.getSkillWeightageForEvaluation(evaluationId))
				.thenReturn(List.of(
						SkillWeightageDAO.builder()
								.skillId("52")
								.weightage(20.0)
								.build()));

		final Map<String, Double> skillWeightages = this.evaluationGenerationRequestToDTOConverter
				.getSkillWeightages(evaluationId, questions);
		Assert.assertEquals(2, skillWeightages.size());
		Assert.assertEquals((Double) 20.0, skillWeightages.get("52"));
		Assert.assertEquals((Double) 0.0, skillWeightages.get(Constants.OTHERS_SKILL_ID));
	}

	/**
	 * All feedback for others category only
	 * and weightages for soft skill category and others
	 */
	@Test
	public void testScenario7() {
		final String evaluationId = "e1";

		final List<QuestionData> questions = List.of(
				QuestionData.builder()
						.feedbacks(
								List.of(
										FeedbackData.builder().categoryId(Constants.OTHERS_SKILL_ID).build(),
										FeedbackData.builder().categoryId(Constants.OTHERS_SKILL_ID).build(),
										FeedbackData.builder().categoryId(Constants.OTHERS_SKILL_ID).build()))
						.build(),
				QuestionData.builder()
						.feedbacks(
								List.of(
										FeedbackData.builder().categoryId(Constants.OTHERS_SKILL_ID).build(),
										FeedbackData.builder().categoryId(Constants.OTHERS_SKILL_ID).build(),
										FeedbackData.builder().categoryId(Constants.OTHERS_SKILL_ID).build()))
						.build());

		when(this.skillWeightageManager.getSkillWeightageForEvaluation(evaluationId))
				.thenReturn(List.of(
						SkillWeightageDAO.builder()
								.skillId("52")
								.weightage(20.0)
								.build()));

		final Map<String, Double> skillWeightages = this.evaluationGenerationRequestToDTOConverter
				.getSkillWeightages(evaluationId, questions);
		Assert.assertEquals(2, skillWeightages.size());
		Assert.assertEquals((Double) 20.0, skillWeightages.get("52"));
		Assert.assertEquals((Double) 80.0, skillWeightages.get(Constants.OTHERS_SKILL_ID));
	}
}
