/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation;

import java.util.NoSuchElementException;

public enum EvaluationPartnerStatus {

	REJECTED("rejected");

	private final String status;

	EvaluationPartnerStatus(final String status) {
		this.status = status;
	}

	public String getValue() {
		return this.status;
	}

	public static EvaluationPartnerStatus fromString(String status) {
		for (EvaluationPartnerStatus es : values()) {
			if (es.getValue().equals(status)) {
				return es;
			}
		}
		throw new NoSuchElementException("Element with value " + status + " has not been found");
	}
}
