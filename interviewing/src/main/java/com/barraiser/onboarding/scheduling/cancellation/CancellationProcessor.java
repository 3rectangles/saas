/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation;

public interface CancellationProcessor {
	void process(CancellationProcessingData data) throws Exception;
}
