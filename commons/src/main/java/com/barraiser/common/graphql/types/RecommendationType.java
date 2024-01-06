/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

public enum RecommendationType {
	NOT_RECOMMENDED("NOT_RECOMMENDED"),

	RECOMMENDED("RECOMMENDED"),

	REQUIRES_FURTHER_REVIEW("REQUIRES_FURTHER_REVIEW"),

	STRONGLY_RECOMMENDED("STRONGLY_RECOMMENDED");

	private final String recommendationType;

	RecommendationType(final String recommendationType) {
		this.recommendationType = recommendationType;
	}

	public String getValue() {
		return this.recommendationType;
	}
}
