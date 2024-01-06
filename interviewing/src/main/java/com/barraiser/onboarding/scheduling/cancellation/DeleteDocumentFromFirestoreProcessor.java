/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

import com.barraiser.onboarding.firebase.FirebaseManager;
import com.barraiser.onboarding.firebase.FirestoreCollection;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.stereotype.Component;

@Log4j2
@AllArgsConstructor
@Component
public class DeleteDocumentFromFirestoreProcessor implements CancellationProcessor {
	private final FirebaseManager firebaseManager;

	@Override
	public void process(CancellationProcessingData data) throws Exception {
		this.firebaseManager
				.getFirestoreDb()
				.collection(FirestoreCollection.INTERVIEW_FEEDBACK.getValue())
				.document(data.getInterviewId())
				.delete();

		this.firebaseManager
				.getFirestoreDb()
				.collection(FirestoreCollection.INTERVIEWING.getValue())
				.document(data.getInterviewId())
				.delete();
	}
}
