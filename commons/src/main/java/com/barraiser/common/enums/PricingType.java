/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.enums;

import java.util.NoSuchElementException;

public enum PricingType {

	JOB_ROLE_BASED("JOB_ROLE_BASED"),

	WORK_EXPERIENCE_BASED("WORK_EXPERIENCE_BASED"),

	FLAT_RATE_BASED("FLAT_RATE_BASED");

	private final String pricingType;

	PricingType(final String pricingType) {
		this.pricingType = pricingType;
	}

	public String getValue() {
		return this.pricingType;
	}

	public static PricingType fromString(String pricingType) {
		for (PricingType pt : values()) {
			if (pt.getValue().equals(pricingType)) {
				return pt;
			}
		}
		throw new NoSuchElementException("Element with value " + pricingType + " has not been found");
	}
}
