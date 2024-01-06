/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import java.util.NoSuchElementException;

public enum EvaluationStatus {
	WAITING_CLIENT("WAITING_CLIENT"), CLIENT_REPLIED("CLIENT_REPLIED"), DONE("Done"), CANCELLED(
			"Cancelled"), EXPERT_NEEDED_FOR_DUMMY_INTERVIEW("Expert_Needed_For_Dummy_Interview"), PENDING_SCHEDULING(
					"pending_scheduling"), WAITING_CANDIDATE("waiting_candidate"), PENDING_INTERVIEWING(
							"pending_interviewing"), WAITING_EXPERT_AVAILABILITY(
									"waiting_expert_availability"), WAITING_SCORE_GENERATION(
											"waiting_score_generation"), PENDING_QC("pending_qc");

	private final String status;

	public Boolean isInProcess() {
		switch (this) {
			case DONE:
			case CANCELLED:
				return false;
		}
		return true;
	}

	EvaluationStatus(final String status) {
		this.status = status;
	}

	public String getValue() {
		return this.status;
	}

	public static EvaluationStatus fromString(String status) {
		for (EvaluationStatus es : values()) {
			if (es.getValue().equals(status)) {
				return es;
			}
		}
		throw new NoSuchElementException("Element with value " + status + " has not been found");
	}
}
