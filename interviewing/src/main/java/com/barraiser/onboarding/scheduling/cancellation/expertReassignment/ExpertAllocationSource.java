/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment;

import java.util.NoSuchElementException;

public enum ExpertAllocationSource {

	MANUAL_EXPERT_REASSIGNMENT("MANUAL_EXPERT_REASSIGNMENT"),

	AUTOMATIC_EXPERT_REASSIGNMENT("AUTOMATIC_EXPERT_REASSIGNMENT"),

	INTERVIEW_SCHEDULING("INTERVIEW_SCHEDULING");

	private final String source;

	ExpertAllocationSource(final String source) {
		this.source = source;
	}

	public String getValue() {
		return this.source;
	}

	public static ExpertAllocationSource fromString(String source) {
		for (ExpertAllocationSource as : values()) {
			if (as.getValue().equals(source)) {
				return as;
			}
		}
		throw new NoSuchElementException("Element with value " + source + " has not been found");
	}
}
