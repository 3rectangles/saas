/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

public enum FieldType {
	SELECT("SELECT"), TEXT("TEXT"), DATE("DATE"), INT("INT"), MULTISELECT("MULTISELECT");

	// TODO: ADD more fieldTypes
	private final String fieldType;

	FieldType(final String fieldType) {
		this.fieldType = fieldType;
	}

	public String getValue() {
		return this.fieldType;
	}
}
