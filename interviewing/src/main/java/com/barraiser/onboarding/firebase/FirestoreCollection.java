/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.firebase;

public enum FirestoreCollection {
	INTERVIEW_FEEDBACK("feedbacks"),

	INTERVIEWING("interviewing");

	private final String collection;

	FirestoreCollection(final String collection) {
		this.collection = collection;
	}

	public String getValue() {
		return this.collection;
	}
}
