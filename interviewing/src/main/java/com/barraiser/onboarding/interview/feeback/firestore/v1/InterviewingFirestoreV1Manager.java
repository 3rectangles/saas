/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.feeback.firestore.v1;

import com.barraiser.onboarding.firebase.FirebaseManager;
import com.barraiser.onboarding.firebase.FirestoreCollection;
import com.barraiser.onboarding.interviewing.InterviewingFirestoreData;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.firestore.DocumentReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Log4j2
@RequiredArgsConstructor
public class InterviewingFirestoreV1Manager {
	private final FirebaseManager firebaseManager;
	private final ObjectMapper objectMapper;

	private DocumentReference getBaseDocRef(final String interviewId) {
		return this.firebaseManager.getFirestoreDb()
				.collection(FirestoreCollection.INTERVIEWING.getValue())
				.document(interviewId);
	}

	public void setBaseDoc(final String interviewId, final InterviewingFirestoreData doc) {
		final Map<String, Object> docToSave = this.objectMapper.convertValue(doc, new TypeReference<>() {
		});
		this.getBaseDocRef(interviewId).set(docToSave);
	}
}
