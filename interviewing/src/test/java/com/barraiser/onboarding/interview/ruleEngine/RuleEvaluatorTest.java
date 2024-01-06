/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.ruleEngine;

import static org.junit.jupiter.api.Assertions.*;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RuleEvaluatorTest {
	private final JSONParser parser = new JSONParser();

	@InjectMocks
	private RuleEvaluator ruleEvaluator;

	@Test
	public void ShouldGiveExpressionEvaluationResultTrue() throws ParseException {

		final String expressionString = "{\"AND\":[{\"AND\":[{\"comparator\":\"lte\",\"actualValue\":100,\"value\":500},{\"comparator\":\"lte\",\"actualValue\":100,\"value\":500}]},{\"OR\":[{\"comparator\":\"lte\",\"actualValue\":100,\"value\":500},{\"comparator\":\"lte\",\"actualValue\":100,\"value\":500}]}]}";
		final String expressionString2 = "{\"AND\":[{\"comparator\":\"gte\",\"actualValue\":null,\"value\":500},{\"OR\":[{\"comparator\":\"lte\",\"actualValue\":null,\"value\":500},{\"comparator\":\"lte\",\"actualValue\":null,\"value\":500}]}]}";
		final JSONObject expression = (JSONObject) parser.parse(expressionString);
		final JSONObject expression2 = (JSONObject) parser.parse(expressionString2);
		assertTrue(ruleEvaluator.evaluate(expression));
		assertTrue(ruleEvaluator.evaluate(expression2));
	}

	@Test
	public void ShouldGiveExpressionEvaluationResultFalse() throws ParseException {

		final String expressionString1 = "{\"AND\":[{\"comparator\":\"gte\",\"actualValue\":100,\"value\":500},{\"OR\":[{\"comparator\":\"lte\",\"actualValue\":100,\"value\":500},{\"comparator\":\"lte\",\"actualValue\":100,\"value\":500}]}]}";
		final JSONObject expression1 = (JSONObject) parser.parse(expressionString1);
		assertFalse(ruleEvaluator.evaluate(expression1));
	}
}
