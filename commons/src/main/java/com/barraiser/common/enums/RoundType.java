/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.enums;

import java.util.NoSuchElementException;

public enum RoundType {

	PEER("PEER"),

	EXPERT("EXPERT"),

	INTERNAL("INTERNAL"),

	MACHINE("MACHINE"),

	MACHINE2("MACHINE2"),

	ROUND3("ROUND3"),

	RECRO("RECRO_1.5"),

	INTRODUCTORY("INTRODUCTORY"),

	FASTTRACK("FASTTRACK");

	private final String roundType;

	RoundType(final String roundType) {
		this.roundType = roundType;
	}

	public String getValue() {
		return this.roundType;
	}

	public static RoundType fromString(String roundType) {
		for (RoundType rt : values()) {
			if (rt.getValue().equals(roundType)) {
				return rt;
			}
		}
		throw new NoSuchElementException("Element with value " + roundType + " has not been found");
	}
}
