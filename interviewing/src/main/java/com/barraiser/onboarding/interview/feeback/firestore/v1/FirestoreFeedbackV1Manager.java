/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.feeback.firestore.v1;

import com.barraiser.onboarding.firebase.FirebaseManager;
import com.barraiser.onboarding.firebase.FirestoreCollection;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.DocumentReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ExecutionException;

@Component
@Log4j2
@RequiredArgsConstructor
public class FirestoreFeedbackV1Manager {
	private final FirebaseManager firebaseManager;
	private final ObjectMapper objectMapper;

	private DocumentReference getBaseDocRef(final String interviewId) {
		return this.firebaseManager.getFirestoreDb()
				.collection(FirestoreCollection.INTERVIEW_FEEDBACK.getValue())
				.document(interviewId);
	}

	public FirestoreFeedbackDocV1 getBaseDoc(final String interviewId) throws ExecutionException, InterruptedException {
		return this.getBaseDocRef(interviewId).get().get().toObject(FirestoreFeedbackDocV1.class);
	}

	public Boolean exists(final String interviewId) throws ExecutionException, InterruptedException {
		return this.getBaseDocRef(interviewId).get().get().exists();
	}

	public void setBaseDoc(final String interviewId, final FirestoreFeedbackDocV1 doc) {
		final Map<String, Object> docToSave = this.objectMapper.convertValue(doc, new TypeReference<>() {
		});
		this.getBaseDocRef(interviewId).set(docToSave);
	}

	public FirestoreFeedbackDocV1.Question getQuestion(final String interviewId, final String questionId)
			throws ExecutionException, InterruptedException {
		return this.getBaseDocRef(interviewId).collection("questions").document(questionId).get().get()
				.toObject(FirestoreFeedbackDocV1.Question.class);
	}

	public FirestoreFeedbackDocV1.Feedback getFeedback(final String interviewId, final String feedbackId)
			throws ExecutionException, InterruptedException {
		return this.getBaseDocRef(interviewId).collection("feedbacks").document(feedbackId).get().get()
				.toObject(FirestoreFeedbackDocV1.Feedback.class);
	}

	public void updateFeedback(final String interviewId, final FirestoreFeedbackDocV1.Feedback feedback) {
		final Map<String, Object> feedbackToSave = this.objectMapper.convertValue(feedback, new TypeReference<>() {
		});
		this.getBaseDocRef(interviewId).collection("feedbacks").document(feedback.getId()).update(feedbackToSave);
	}

	public void updateQuestion(final String interviewId, final FirestoreFeedbackDocV1.Question question) {
		final Map<String, Object> questionToSave = this.objectMapper.convertValue(question, new TypeReference<>() {
		});
		this.getBaseDocRef(interviewId).collection("questions").document(question.getId()).update(questionToSave);
	}

	public void saveFeedback(final String interviewId, final FirestoreFeedbackDocV1.Feedback feedback) {
		final Map<String, Object> feedbackToSave = this.objectMapper.convertValue(feedback, new TypeReference<>() {
		});
		this.getBaseDocRef(interviewId).collection("feedbacks").document(feedback.getId()).set(feedbackToSave);
	}

	public void saveQuestion(final String interviewId, final FirestoreFeedbackDocV1.Question question) {
		final Map<String, Object> questionToSave = this.objectMapper.convertValue(question, new TypeReference<>() {
		});
		this.getBaseDocRef(interviewId).collection("questions").document(question.getId()).set(questionToSave);
	}
}
