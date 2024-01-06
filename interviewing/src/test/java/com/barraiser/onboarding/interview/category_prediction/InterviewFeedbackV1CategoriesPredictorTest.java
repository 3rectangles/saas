/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.category_prediction;

import com.barraiser.common.graphql.types.InterviewCategory;
import com.barraiser.common.responses.QuestionCategoryPredictionResponse;
import com.barraiser.onboarding.common.Constants;
import com.barraiser.onboarding.dal.*;
import com.barraiser.onboarding.dataScience.QuestionCategoryPredictionGenerator;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewCategoriesDataFetcher;
import com.barraiser.onboarding.interview.feeback.category_prediction.v1.InterviewFeedbackV1CategoriesPredictor;
import com.barraiser.onboarding.interview.feeback.category_prediction.v1.InterviewFeedbackV1PredictedCategorySaver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class InterviewFeedbackV1CategoriesPredictorTest {

	@InjectMocks
	private InterviewFeedbackV1CategoriesPredictor interviewFeedbackV1CategoriesPredictor;

	@Mock
	private InterViewRepository interViewRepository;

	@Mock
	private FeedbackRepository feedbackRepository;

	@Mock
	private QuestionRepository questionRepository;

	@Mock
	private InterviewCategoriesDataFetcher interviewCategoriesDataFetcher;

	@Mock
	private QuestionCategoryPredictionGenerator questionCategoryPredictionGenerator;

	@Mock
	private InterviewFeedbackV1PredictedCategorySaver interviewFeedbackV1PredictedCategorySaver;

	@Captor
	private ArgumentCaptor<List<FeedbackDAO>> feedbacksCaptor;

	/**
	 * No Others category configured in Interview structure for round.
	 */
	@Test
	public void shouldCallDSModelForCategoryPredictionScenario1() {

		when(this.interViewRepository.findById(any()))
				.thenReturn(Optional.of(InterviewDAO.builder().id("interview_1").evaluationId("eval_1").build()));

		when(this.questionRepository
				.findAllByInterviewIdAndRescheduleCount(any(), any())).thenReturn(
						List.of(
								QuestionDAO.builder()
										.id("q1")
										.build(),
								QuestionDAO.builder()
										.id("q2")
										.build(),
								QuestionDAO.builder()
										.id("q3")
										.build()));

		when(this.feedbackRepository.findAllByReferenceId(any())).thenReturn(
				List.of(
						FeedbackDAO.builder()
								.referenceId("q1")
								.build(),
						FeedbackDAO.builder()
								.referenceId("q1")
								.build(),
						FeedbackDAO.builder()
								.referenceId("q2")
								.build(),
						FeedbackDAO.builder()
								.referenceId("q3")
								.build()));

		when(this.interviewCategoriesDataFetcher.getParentInterviewCategoryOfSkills("interview_structure_1"))
				.thenReturn(
						List.of(
								InterviewCategory.builder()
										.id("lld")
										.build(),
								InterviewCategory.builder()
										.id("ps")
										.build(),
								InterviewCategory.builder()
										.id("dsa")
										.build()));

		when(this.questionCategoryPredictionGenerator.predictQuestionCategories(any()))
				.thenReturn(QuestionCategoryPredictionResponse
						.builder()
						.questionIdToQuestionCategoryMap(new HashMap<>())
						.build());

		this.interviewFeedbackV1CategoriesPredictor.predictCategories("interview_1");

		verify(this.questionCategoryPredictionGenerator, times(1)).predictQuestionCategories(any());
	}

	/**
	 * Others category configured in Interview structure for round. But other
	 * categories also present
	 */
	@Test
	public void shouldCallDSModelForCategoryPredictionScenario2() {

		when(this.interViewRepository.findById(any()))
				.thenReturn(Optional.of(InterviewDAO.builder().id("interview_1").evaluationId("eval_1").build()));

		when(this.questionRepository
				.findAllByInterviewIdAndRescheduleCount(any(), any())).thenReturn(
						List.of(
								QuestionDAO.builder()
										.id("q1")
										.build(),
								QuestionDAO.builder()
										.id("q2")
										.build(),
								QuestionDAO.builder()
										.id("q3")
										.build()));

		when(this.feedbackRepository.findAllByReferenceId(any())).thenReturn(
				List.of(
						FeedbackDAO.builder()
								.referenceId("q1")
								.build(),
						FeedbackDAO.builder()
								.referenceId("q1")
								.build(),
						FeedbackDAO.builder()
								.referenceId("q2")
								.build(),
						FeedbackDAO.builder()
								.referenceId("q3")
								.build()));

		when(this.interviewCategoriesDataFetcher.getParentInterviewCategoryOfSkills("interview_structure_1"))
				.thenReturn(
						List.of(
								InterviewCategory.builder()
										.id("lld")
										.build(),
								InterviewCategory.builder()
										.id("ps")
										.build(),
								InterviewCategory.builder()
										.id(Constants.OTHERS_SKILL_ID)
										.build()));

		when(this.questionCategoryPredictionGenerator.predictQuestionCategories(any()))
				.thenReturn(QuestionCategoryPredictionResponse
						.builder()
						.questionIdToQuestionCategoryMap(new HashMap<>())
						.build());

		this.interviewFeedbackV1CategoriesPredictor.predictCategories("interview_1");

		verify(this.questionCategoryPredictionGenerator, times(1)).predictQuestionCategories(any());
	}

	/**
	 * ONLY Others category configured in Interview structure for round.
	 */
	@Test
	public void shouldNOTCallDSModelForCategoryPredictionScenario1() {

		when(this.interViewRepository.findById(any()))
				.thenReturn(Optional.of(InterviewDAO.builder().id("interview_1")
						.interviewStructureId("interview_structure_1")
						.evaluationId("eval_1").build()));

		when(this.questionRepository
				.findAllByInterviewIdAndRescheduleCount(any(), any())).thenReturn(
						List.of(
								QuestionDAO.builder()
										.id("q1")
										.build(),
								QuestionDAO.builder()
										.id("q2")
										.build(),
								QuestionDAO.builder()
										.id("q3")
										.build()));

		when(this.feedbackRepository.findAllByReferenceId(any())).thenReturn(
				List.of(
						FeedbackDAO.builder()
								.referenceId("q1")
								.build(),
						FeedbackDAO.builder()
								.referenceId("q1")
								.build(),
						FeedbackDAO.builder()
								.referenceId("q2")
								.build(),
						FeedbackDAO.builder()
								.referenceId("q3")
								.build()));

		when(this.interviewCategoriesDataFetcher.getParentInterviewCategoryOfSkills(any())).thenReturn(
				List.of(
						InterviewCategory.builder()
								.id(Constants.OTHERS_SKILL_ID)
								.build()));

		doNothing().when(this.interviewFeedbackV1PredictedCategorySaver).savePredictedCategories(any(), any());

		this.interviewFeedbackV1CategoriesPredictor.predictCategories("interview_1");

		verify(this.questionCategoryPredictionGenerator, times(0)).predictQuestionCategories(any());
	}

	@Test
	public void testScenario2() {

		verify(this.feedbackRepository).saveAll(this.feedbacksCaptor.capture());

		final List<FeedbackDAO> capturedFeedback = this.feedbacksCaptor.getValue();
	}

}
