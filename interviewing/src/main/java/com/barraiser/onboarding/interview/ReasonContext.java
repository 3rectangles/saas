/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import java.util.NoSuchElementException;

public enum ReasonContext {

	CANCELLATION("CANCELLATION"),

	WAITING("WAITING"),

	REOPEN("REOPEN"),

	REDO("REDO"),

	WAITING_CLIENT("WAITING_CLIENT"),

	CANDIDATURE_REJECTION("CANDIDATE_REJECTION");

	private final String reasonContext;

	ReasonContext(final String reasonContext) {
		this.reasonContext = reasonContext;
	}

	public String getValue() {
		return this.reasonContext;
	}

	public static ReasonContext fromString(final String reasonContext) {
		for (ReasonContext rc : values()) {
			if (rc.getValue().equals(reasonContext)) {
				return rc;
			}
		}
		throw new NoSuchElementException("Element with value " + reasonContext + " has not been found");
	}
}
