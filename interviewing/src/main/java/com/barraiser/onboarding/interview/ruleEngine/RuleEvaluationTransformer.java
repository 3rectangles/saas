/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.ruleEngine;

import com.barraiser.onboarding.interview.ruleEngine.enums.Operator;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Component
@RequiredArgsConstructor
public class RuleEvaluationTransformer {

	public final List<RulePopulator> rulePopulators;
	private final List<String> operators = Arrays.stream(Operator.values())
			.map(op -> op.getOperator())
			.collect(Collectors.toList());

	public void process(RuleProcessingData ruleProcessingData) {

		ruleProcessingData.setTransformedRuleBody(
				this.generateEvaluatableExpression(
						ruleProcessingData.getRuleBody(), ruleProcessingData));
	}

	public JSONObject generateEvaluatableExpression(
			final JSONObject expression, RuleProcessingData ruleProcessingData) {

		if (containsOperator(expression)) {
			final String operator = (String) expression.keySet().stream().findFirst().get();
			final JSONObject resultingExpression = new JSONObject();
			final JSONArray subExpressions = (JSONArray) expression.get(operator);
			final JSONArray resultingSubExpression = new JSONArray();
			subExpressions.forEach(
					e -> resultingSubExpression.add(
							generateEvaluatableExpression((JSONObject) e, ruleProcessingData)));
			resultingExpression.put(operator, resultingSubExpression);
			return resultingExpression;
		} else {
			final JSONObject resultingExpression = processCriteria(expression, ruleProcessingData);
			return resultingExpression;
		}
	}

	private JSONObject processCriteria(
			JSONObject expression, RuleProcessingData ruleProcessingData) {
		final JSONObject resultingExpression = new JSONObject();
		final String comparator = (String) expression.get("comparator");
		final Object value = expression.get("value");
		final String type = (String) expression.get("type");
		final JSONArray entityPath = (JSONArray) expression.get("entity_attributes");
		final Object actualValue = getActualValue(ruleProcessingData, type, entityPath);
		resultingExpression.put("comparator", comparator);
		resultingExpression.put("value", value);
		resultingExpression.put("actualValue", actualValue);
		return resultingExpression;
	}

	public Object getActualValue(
			RuleProcessingData ruleProcessingData, String type, JSONArray entityPath) {
		final RulePopulator rulePopulator = rulePopulators.stream()
				.filter(p -> p.ruleType().equals(ruleProcessingData.getRuleType()))
				.findFirst()
				.get();
		final Map<String, Object> valuesMap = new HashMap<>();
		valuesMap.put("type", type);
		valuesMap.put("entityPath", entityPath);
		valuesMap.put("entityId", ruleProcessingData.getEntityId());
		final Object actualValue = rulePopulator.populate(valuesMap);
		return actualValue;
	}

	public boolean containsOperator(JSONObject expression) {
		return (expression).keySet().stream().anyMatch(operators::contains);
	}
}
