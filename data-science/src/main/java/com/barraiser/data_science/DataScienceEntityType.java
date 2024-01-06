/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science;

public enum DataScienceEntityType {
	FEEDBACK_RECOMMENDATION("DS_FEEDBACK_RECOMMENDATION"),

	QUESTION_CATEGORY_PREDICTION("DS_QUESTION_CATEGORY_PREDICTION"),

	OVERALL_FEEDBACK_STRENGTHS("DS_OVERALL_FEEDBACK_STRENGTHS"),

	OVERALL_FEEDBACK_AREAS_OF_IMPROVEMENTS("DS_OVERALL_FEEDBACK_AREAS_OF_IMPROVEMENTS");

	private final String dataScienceEntityType;

	DataScienceEntityType(final String dataScienceEntityType) {
		this.dataScienceEntityType = dataScienceEntityType;
	}

	public String getValue() {
		return this.dataScienceEntityType;
	}
}
