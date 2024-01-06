/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.ruleEngine;

import com.barraiser.onboarding.interview.ruleEngine.enums.Operator;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Log4j2
@Component
@RequiredArgsConstructor
public class RuleEvaluator {

	List<String> operatorList = Arrays.stream(Operator.values())
			.map(op -> op.getOperator())
			.collect(Collectors.toList());

	public boolean process(RuleProcessingData data) throws ParseException {

		return this.evaluate(data.getTransformedRuleBody());
	}

	public boolean evaluate(JSONObject expression) throws ParseException {

		if (this.containsOperator(expression)) {
			final String operator = (String) (expression)
					.keySet().stream()
					.filter(operatorList::contains)
					.findFirst()
					.get();
			final JSONArray subExpressions = (JSONArray) (expression).get(operator);
			final List<Boolean> results = new ArrayList<>();
			for (Object subExpression : subExpressions) {
				results.add(evaluate((JSONObject) subExpression));
			}
			return computeResult(operator, results);
		} else {
			return this.evaluateExpression((JSONObject) expression);
		}
	}

	public boolean containsOperator(Object expression) {
		return ((JSONObject) expression).keySet().stream().anyMatch(operatorList::contains);
	}

	public boolean computeResult(String operator, List<Boolean> results) {
		switch (operator) {
			case "AND":
				return results.stream().allMatch(result -> result.equals(true));
			case "OR":
				return results.stream().anyMatch(result -> result.equals(true));
			case "NOT":
				return results.stream().findFirst().get().equals(false);
			default:
				return true;
		}
	}

	public boolean evaluateExpression(JSONObject expression) {
		final String comparator = (String) expression.get("comparator");
		final Object value = expression.get("value");
		final Object actualValue = expression.get("actualValue");
		switch (comparator) {
			case "gte":
				return actualValue == null || ((Long) actualValue >= (Long) value);
			case "lte":
				return actualValue == null || ((Long) actualValue <= (Long) value);
			default:
				return false;
		}
	}
}
