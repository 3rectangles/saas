/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.common.responses.FollowupQuestionDetectionResponse;
import com.barraiser.onboarding.firebase.FirebaseManager;
import com.barraiser.onboarding.firebase.FirestoreCollection;
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
public class FollowupQuestionMarker {
	private final FirebaseManager firebaseManager;

	public void markFollowupQuestionsForInterview(
			final String interviewId,
			final FollowupQuestionDetectionResponse followupQuestionDetectionResponse)
			throws Exception {
		for (Map.Entry<String, Boolean> entry : followupQuestionDetectionResponse.getQuestionIdToIsFollowUpMap()
				.entrySet()) {
			final String questionId = entry.getKey();
			final Boolean isFollowup = entry.getValue();

			this.updateIsFollowupFlagOfQuestionInFirestore(
					interviewId,
					questionId,
					isFollowup);
		}
	}

	private void updateIsFollowupFlagOfQuestionInFirestore(
			final String interviewId,
			final String questionId,
			final Boolean isFollowup) throws Exception {
		ApiFuture<DocumentSnapshot> questionDocument = this.firebaseManager
				.getFirestoreDb()
				.collection(FirestoreCollection.INTERVIEW_FEEDBACK
						.getValue())
				.document(interviewId)
				.collection("questions")
				.document(questionId)
				.get();

		Map<String, Object> fieldToUpdatedValue = (Map<String, Object>) questionDocument.get().get("overview");

		fieldToUpdatedValue.put("isFollowUp", isFollowup);

		Map<String, Object> updatedOverviewFields = new HashMap<>();
		updatedOverviewFields.put("overview", fieldToUpdatedValue);

		this.firebaseManager
				.getFirestoreDb()
				.collection(FirestoreCollection.INTERVIEW_FEEDBACK
						.getValue())
				.document(interviewId)
				.collection("questions")
				.document(questionId)
				.update(updatedOverviewFields);
	}
}
