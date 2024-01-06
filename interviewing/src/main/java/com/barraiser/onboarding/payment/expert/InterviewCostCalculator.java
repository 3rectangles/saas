/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.payment.expert;

public interface InterviewCostCalculator {
	String status();

	String version();

	Double calculate(final InterviewPaymentCalculationData data);
}
