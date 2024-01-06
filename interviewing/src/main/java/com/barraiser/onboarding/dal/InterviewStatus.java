/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import java.util.NoSuchElementException;

public enum InterviewStatus {
	/**
	 * User has blocked an interviewer but has not completed the interview yet.
	 */
	PENDING_PAYMENT("PENDING_PAYMENT"),

	PENDING_SCHEDULING("pending_scheduling"),

	SLOT_REQUESTED_BY_CANDIDATE("SLOT_REQUESTED_BY_CANDIDATE"),

	PENDING_TA_ASSIGNMENT("pending_ta_assignment"),

	PENDING_FEEDBACK_SUBMISSION("pending_feedback_submission"),

	PENDING_INTERVIEWING("pending_interviewing"),

	PENDING_TAGGING("pending_tagging"),

	PENDING_QC("pending_qc"),

	PENDING_DECISION("pending_decision"),

	/**
	 * Interview process is over.
	 */
	COMPLETED("COMPLETED"),

	CONFIRMED("CONFIRMED"),

	DONE("Done"),

	PENDING_CORRECTION("pending_correction"),

	PENDING_DATA_ENTRY("PENDING_DATA_ENTRY_2"),

	/**
	 * Interview has been cancelled.
	 */
	CANCELLATION_DONE("cancellation_done"),

	EXPERT_NEEDED_FOR_DUMMY_INTERVIEW("Expert_Needed_For_Dummy_Interview");

	private final String status;

	InterviewStatus(final String status) {
		this.status = status;
	}

	public String getValue() {
		return this.status;
	}

	public static InterviewStatus fromString(String status) {
		for (InterviewStatus is : values()) {
			if (is.getValue().equals(status)) {
				return is;
			}
		}
		throw new NoSuchElementException("Element with value " + status + " has not been found");
	}

	public Boolean isScheduled() {
		switch (this) {
			case PENDING_TA_ASSIGNMENT:
			case PENDING_INTERVIEWING:
			case PENDING_FEEDBACK_SUBMISSION:
			case PENDING_DECISION:
			case PENDING_TAGGING:
			case PENDING_QC:
			case EXPERT_NEEDED_FOR_DUMMY_INTERVIEW:
				return true;
		}
		return false;
	}

	public Boolean isFeedbackSubmissionCompleted() {
		switch (this) {
			case PENDING_CORRECTION:
			case PENDING_QC:
			case DONE:
				return true;
		}

		return false;
	}

	public Boolean isInProcess() {
		switch (this) {
			case DONE:
			case CANCELLATION_DONE:
				return false;
		}
		return true;
	}

	public Boolean isPendingScheduling() {
		switch (this) {
			case PENDING_SCHEDULING:
			case EXPERT_NEEDED_FOR_DUMMY_INTERVIEW:
			case SLOT_REQUESTED_BY_CANDIDATE:
			case PENDING_DATA_ENTRY:
				return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	public Boolean isConductionPending() {
		switch (this) {
			case PENDING_SCHEDULING:
			case EXPERT_NEEDED_FOR_DUMMY_INTERVIEW:
			case SLOT_REQUESTED_BY_CANDIDATE:
			case PENDING_DATA_ENTRY:
			case PENDING_TA_ASSIGNMENT:
				return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	public Boolean isDone() {
		return this.equals(DONE);
	}

	public Boolean isPendingQC() {
		return this.equals(PENDING_QC);
	}

	public Boolean isPendingCorrection() {
		return this.equals(PENDING_CORRECTION);
	}
}
