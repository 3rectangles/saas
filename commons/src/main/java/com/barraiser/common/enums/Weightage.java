/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.enums;

import java.util.NoSuchElementException;

public enum Weightage {

	EASY("EASY"),

	MODERATE("MODERATE"),

	HARD("HARD");

	private final String weightage;

	Weightage(final String weightage) {
		this.weightage = weightage;
	}

	public String getValue() {
		return this.weightage;
	}

	public static Weightage fromString(final String weightage) {
		for (final Weightage qt : values()) {
			if (qt.getValue().equals(weightage)) {
				return qt;
			}
		}
		throw new NoSuchElementException(
				"Element with value " + weightage + " has not been found");
	}
}
