/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.ruleEngine;

import lombok.AllArgsConstructor;

import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class RuleEngineOrchestrator {

	private final RuleEvaluationTransformer ruleEvaluationTransformer;
	private final RuleEvaluator ruleEvaluator;

	public boolean process(RuleProcessingData ruleProcessingData) throws ParseException {

		ruleEvaluationTransformer.process(ruleProcessingData);
		return ruleEvaluator.process(ruleProcessingData);
	}
}
