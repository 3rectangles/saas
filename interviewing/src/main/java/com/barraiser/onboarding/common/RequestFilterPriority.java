/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.common;

public class RequestFilterPriority {
	public final static int Authentication = 1;
	public final static int LoggingContextSetup = 2;
	public final static int FeatureToggles = 3;
	public final static int FirebaseAuth = 4;
	public final static int contextPopulation = 5;
}
