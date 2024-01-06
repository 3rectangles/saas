/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.feeback.category_prediction.v1;

import com.barraiser.common.DTO.QuestionCategoryDTO;
import com.barraiser.common.responses.QuestionCategoryPredictionResponse;
import com.barraiser.onboarding.dal.FeedbackDAO;
import com.barraiser.onboarding.dal.FeedbackRepository;
import com.barraiser.onboarding.dal.InterviewDAO;
import com.barraiser.onboarding.interview.InterViewRepository;
import com.barraiser.onboarding.interview.InterviewUtil;
import com.barraiser.onboarding.interview.feeback.firestore.v1.FirestoreFeedbackDocV1;
import com.barraiser.onboarding.interview.feeback.firestore.v1.FirestoreFeedbackV1Manager;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class InterviewFeedbackV1PredictedCategorySaver {

	private final FeedbackRepository feedbackRepository;
	private final InterViewRepository interViewRepository;
	private final InterviewUtil interviewUtil;

	private final FirestoreFeedbackV1Manager firestoreFeedbackManager;

	public void savePredictedCategories(final String interviewId,
			final QuestionCategoryPredictionResponse predictionResponse) {
		final InterviewDAO interview = this.interViewRepository.findById(interviewId).get();
		for (Map.Entry<String, QuestionCategoryDTO> entry : predictionResponse.getQuestionIdToQuestionCategoryMap()
				.entrySet()) {
			if (this.interviewUtil.isSaasInterview(interview.getInterviewRound())) {
				this.savePredictedCategoryInDb(entry.getKey(), entry.getValue().getId());
				this.addCategoryInFirestore(interviewId, entry.getKey(), entry.getValue().getId());
			} else {
				this.savePredictedCategoryInFirestore(interviewId, entry.getKey(), entry.getValue().getId());
			}
		}
	}

	private void savePredictedCategoryInDb(final String questionId, final String categoryId) {
		final List<FeedbackDAO> feedbacks = this.feedbackRepository.findAllByReferenceId(questionId);
		this.feedbackRepository.saveAll(feedbacks.stream().map(
				f -> f.toBuilder()
						.categoryId(f.getCategoryId() != null ? f.getCategoryId() : categoryId)
						.build())
				.collect(Collectors.toList()));
	}

	private void savePredictedCategoryInFirestore(final String interviewId, final String questionId,
			final String categoryId) {
		try {
			final FirestoreFeedbackDocV1.Question question = this.firestoreFeedbackManager.getQuestion(interviewId,
					questionId);
			this.firestoreFeedbackManager.updateQuestion(interviewId,
					question.toBuilder().categoryPredicted(categoryId).build());
		} catch (ExecutionException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void addCategoryInFirestore(final String interviewId, final String questionId,
			final String categoryId) {
		try {
			final FirestoreFeedbackDocV1.Question question = this.firestoreFeedbackManager.getQuestion(interviewId,
					questionId);
			if (question.getFeedbackIds() == null || question.getFeedbackIds().isEmpty()) {
				final FirestoreFeedbackDocV1.Feedback feedback = FirestoreFeedbackDocV1.Feedback.builder()
						.id(UUID.randomUUID().toString())
						.questionId(questionId)
						.categoryId(categoryId)
						.build();
				this.firestoreFeedbackManager.saveFeedback(interviewId, feedback);
				this.firestoreFeedbackManager.updateQuestion(interviewId, question.toBuilder()
						.feedbackIds(List.of(feedback.getId())).build());
				return;
			}
			for (String feedbackId : question.getFeedbackIds()) {
				final FirestoreFeedbackDocV1.Feedback feedback = this.firestoreFeedbackManager.getFeedback(interviewId,
						feedbackId);
				if (feedback.getCategoryId() == null) {
					this.firestoreFeedbackManager.updateFeedback(interviewId,
							feedback.toBuilder().categoryId(categoryId).build());
				}
			}
		} catch (ExecutionException | InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
