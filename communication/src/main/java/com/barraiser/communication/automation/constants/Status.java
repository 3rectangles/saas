/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.constants;

public enum Status {
	SUCCESS("SUCCESS"),

	FAILED("FAILED"),

	SKIPPED("SKIPPED");

	private final String status;

	Status(final String status) {
		this.status = status;
	}

	public String getValue() {
		return this.status;
	}
}
