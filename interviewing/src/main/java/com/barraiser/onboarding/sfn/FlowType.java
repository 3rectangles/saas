/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.sfn;

/**
 * This is for the types of
 * flows we have in the step functions
 * currently
 */
public enum FlowType {

	INTERVIEW_CANCELLATION("INTERVIEW_CANCELLATION"),

	INTERVIEW_CONFIRMATION("INTERVIEW_CONFIRMATION"),

	INTERVIEW_SCHEDULING("INTERVIEW_SCHEDULING"),

	EXPERT_ALLOCATION("EXPERT_ALLOCATION"),

	EXPERT_DEALLOCATION("EXPERT_DEALLOCATION"),

	EXPERT_REASSIGNMENT("EXPERT_REASSIGNMENT"),

	TA_ALLOCATION("TA_ALLOCATION"),

	INTERVIEWING_LIFECYCLE("INTERVIEWING_LIFECYCLE"),

	CANDIDATE_FOLLOW_UP_FOR_SCHEDULING("CANDIDATE_FOLLOW_UP_FOR_SCHEDULING"),

	EXPERT_REMINDER("EXPERT_REMINDER");

	private final String flowType;

	FlowType(final String flowType) {
		this.flowType = flowType;
	}

	public String getValue() {
		return this.flowType;
	}

}
