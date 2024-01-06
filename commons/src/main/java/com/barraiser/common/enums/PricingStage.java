/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.enums;

import java.util.NoSuchElementException;

public enum PricingStage {

	FREE_DEMO("FREE_DEMO"),

	PAID_DEMO("PAID_DEMO"),

	CONTRACTUAL("CONTRACTUAL");

	private final String stage;

	PricingStage(final String stage) {
		this.stage = stage;
	}

	public String getValue() {
		return this.stage;
	}

	public static PricingStage fromString(String stage) {
		for (PricingStage st : values()) {
			if (st.getValue().equals(stage)) {
				return st;
			}
		}
		throw new NoSuchElementException("Element with value " + stage + " has not been found");
	}
}
