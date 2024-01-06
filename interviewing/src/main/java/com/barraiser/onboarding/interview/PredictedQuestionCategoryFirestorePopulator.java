/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.DTO.QuestionCategoryDTO;
import com.barraiser.common.responses.QuestionCategoryPredictionResponse;
import com.barraiser.onboarding.firebase.FirebaseManager;
import com.barraiser.onboarding.firebase.FirestoreCollection;
import com.barraiser.onboarding.interview.pojo.InterviewQuestionDetails;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Component
@Log4j2
@AllArgsConstructor
public class PredictedQuestionCategoryFirestorePopulator {
	private final FirebaseManager firebaseManager;

	public void addPredictedQuestionCategoryToQuestionsInFirestore(
			final String interviewId,
			final QuestionCategoryPredictionResponse questionCategoryPredictionResponse) throws Exception {
		log.info(String.format(
				"Adding predictedQuestionId to Question in Firestore for interviewId:%s",
				interviewId));

		for (Map.Entry<String, QuestionCategoryDTO> entry : questionCategoryPredictionResponse
				.getQuestionIdToQuestionCategoryMap()
				.entrySet()) {
			final String questionId = entry.getKey();
			final QuestionCategoryDTO predictedQuestionCategoryDTO = entry.getValue();

			this.addPredictionQuestionCategoryIdInFirestore(
					interviewId,
					questionId,
					predictedQuestionCategoryDTO.getId());
		}
	}

	private void addPredictionQuestionCategoryIdInFirestore(
			final String interviewId,
			final String questionId,
			final String predictedQuestionCategoryId) throws Exception {
		ApiFuture<DocumentSnapshot> questionDocument = this.firebaseManager
				.getFirestoreDb()
				.collection(FirestoreCollection.INTERVIEW_FEEDBACK
						.getValue())
				.document(interviewId)
				.collection("questions")
				.document(questionId)
				.get();

		Map<String, Object> fieldToValue = (Map<String, Object>) questionDocument.get().get("overview");

		fieldToValue.put("predictedQuestionCategoryId", predictedQuestionCategoryId);

		Map<String, Object> updatedOverviewFields = new HashMap<>();
		updatedOverviewFields.put("overview", fieldToValue);

		this.firebaseManager
				.getFirestoreDb()
				.collection(FirestoreCollection.INTERVIEW_FEEDBACK.getValue())
				.document(interviewId)
				.collection("questions")
				.document(questionId)
				.update(updatedOverviewFields);
	}
}
