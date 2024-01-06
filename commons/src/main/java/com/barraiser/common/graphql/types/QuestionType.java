/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

public enum QuestionType {
	REQUIRED("REQUIRED"),

	GOOD_TO_KNOW("GOOD_TO_KNOW"),

	DELETED("DELETED"),

	NON_EVALUATIVE("NON_EVALUATIVE");

	private final String questionType;

	QuestionType(final String questionType) {
		this.questionType = questionType;
	}

	public String getValue() {
		return this.questionType;
	}
}
