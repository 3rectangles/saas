/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interviewing;

import com.barraiser.onboarding.firebase.FirebaseManager;
import com.barraiser.onboarding.firebase.FirestoreCollection;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
@Log4j2
public class InterviewingFirestoreDataFetcher {
	private final FirebaseManager firebaseManager;
	private final ObjectMapper objectMapper;

	public InterviewingFirestoreData get(final String interviewId) throws ExecutionException, InterruptedException {
		final ApiFuture<DocumentSnapshot> document = this.firebaseManager.getFirestoreDb()
				.collection(FirestoreCollection.INTERVIEWING.getValue())
				.document(interviewId)
				.get();

		return this.objectMapper.convertValue(document.get().getData(), InterviewingFirestoreData.class);
	}
}
