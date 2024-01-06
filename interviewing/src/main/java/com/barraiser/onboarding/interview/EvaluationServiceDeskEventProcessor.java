/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview;

import com.barraiser.onboarding.interview.jira.evaluation.EvaluationServiceDeskProcessingData;

public interface EvaluationServiceDeskEventProcessor {
	void process(EvaluationServiceDeskProcessingData data) throws Exception;
}
