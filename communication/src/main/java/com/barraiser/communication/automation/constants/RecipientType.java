/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.communication.automation.constants;

public enum RecipientType {
	CANDIDATE("CANDIDATE"),

	EXPERT("EXPERT"),

	PARTNER("PARTNER"),

	OPERATIONS("OPERATIONS"),

	MENTION("MENTION");

	private final String recipientType;

	RecipientType(final String recipientType) {
		this.recipientType = recipientType;
	}

	public String getValue() {
		return this.recipientType;
	}
}
