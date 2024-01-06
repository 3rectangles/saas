/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.entity;

public enum EntityType {
	INTERVIEW("INTERVIEW"),

	EVALUATION("EVALUATION"),

	PARTNER("PARTNER"),

	ENTITY("ENTITY"),

	JOB_ROLE("JOB_ROLE"),

	TEAM("TEAM"),

	HIRING_MANAGER("HIRING_MANAGER"),

	LOCATION("LOCATION"),

	RECRUITER("RECRUITER"),

	ATS_STATUS("ATS_STATUS"),

	EMPTY("EMPTY"),

	USER("USER");

	private final String entityType;

	EntityType(final String entityType) {
		this.entityType = entityType;
	}

	public String getValue() {
		return this.entityType;
	}
}
