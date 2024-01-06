/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.feeback;

import com.barraiser.onboarding.firebase.FirebaseManager;
import com.barraiser.onboarding.firebase.FirestoreCollection;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@Log4j2
@AllArgsConstructor
public class InterviewFeedbackVersionFetcher {
	private final FirebaseManager firebaseManager;

	private DocumentReference getBaseDocRef(final String interviewId) {
		return this.firebaseManager.getFirestoreDb()
				.collection(FirestoreCollection.INTERVIEW_FEEDBACK.getValue())
				.document(interviewId);
	}

	public Integer getFeedbackVersion(final String interviewId) {
		final DocumentSnapshot interviewFeedbackDoc;
		try {
			interviewFeedbackDoc = this.getBaseDocRef(interviewId).get().get();
			final Long version = (Long) interviewFeedbackDoc.get("version");
			return version == null ? 0 : version.intValue();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
}
