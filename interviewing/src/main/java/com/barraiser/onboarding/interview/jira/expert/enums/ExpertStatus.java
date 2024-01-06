/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jira.expert.enums;

public enum ExpertStatus {
	APPROVED("Approved");

	private final String status;

	ExpertStatus(String status) {

		this.status = status;
	}

	public String getValue() {
		return this.status;
	}
}
