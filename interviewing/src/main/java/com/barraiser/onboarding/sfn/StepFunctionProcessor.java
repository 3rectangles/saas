/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.sfn;

public interface StepFunctionProcessor<T> {

	String getFlowIdentifier(T data);

	void process(T data) throws Exception;

}
