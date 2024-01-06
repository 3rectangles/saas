/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.ruleEngine.enums;

import lombok.Getter;

public enum Operator {
	AND("AND"), OR("OR"), NOT("NOT");

	@Getter
	private final String operator;

	Operator(final String operator) {
		this.operator = operator;
	}
}
