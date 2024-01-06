/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.jobrole;

import java.util.Locale;

public enum JobRoleCategory {
	A("A"), B("B"), C("C"), D("D"), E("E"), F("F");

	private final String category;

	JobRoleCategory(String category) {

		this.category = category;
	}

	public String getValue() {
		return this.category;
	}
}
