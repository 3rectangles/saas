/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

public enum WaitingClientReason {
	CANDIDATE_IS_PENDING_FOR_SCHEDULING("Candidate is pending for scheduling"), ASKED_TO_KEEP_THE_CANDIDATE_ON_HOLD(
			"Asked to keep the candidate on hold");

	private final String status;

	WaitingClientReason(final String status) {
		this.status = status;
	}

	public String getValue() {
		return this.status;
	}
}
