/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.ruleEngine;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class RuleEvaluationTransformerTest {

	private final JSONParser parser = new JSONParser();

	@InjectMocks
	private RuleEvaluationTransformer ruleEvaluationTransformer;

	@Mock
	private SelectionCriteriaPopulator selectionCriteriaPopulator;

	private List<RulePopulator> rulePopulators = new ArrayList<>();

	@Before
	public void setup() {

		MockitoAnnotations.initMocks(this);
		rulePopulators = Arrays.asList(selectionCriteriaPopulator);
		ReflectionTestUtils.setField(ruleEvaluationTransformer,
				"rulePopulators",
				rulePopulators);
	}

	@Test
	public void shouldGenerateProperExpression() throws ParseException {
		when(this.selectionCriteriaPopulator.populate(anyMap())).thenReturn((long) 100);
		when(this.selectionCriteriaPopulator.ruleType()).thenReturn("SELECTION_CRITERIA");

		RuleProcessingData ruleProcessingData = RuleProcessingData.builder().ruleType("SELECTION_CRITERIA").build();
		final String expressionString = "{\"AND\":[{\"comparator\":\"gte\",\"value\":500,\"type\":\"ROUND_LEVEL_SCORE\",\"entity_attributes\":[{\"key\":\"roundIndex\",\"value\":0}]},{\"OR\":[{\"comparator\":\"lte\",\"type\":\"SKILL_LEVEL_SCORE\",\"value\":500,\"entity_attributes\":[{\"key\":\"roundIndex\",\"value\":1},{\"key\":\"skillId\",\"value\":\"123-dff\"}]},{\"comparator\":\"lte\",\"type\":\"SKILL_LEVEL_SCORE\",\"value\":500,\"entity_attributes\":[{\"key\":\"roundIndex\",\"value\":1},{\"key\":\"skillId\",\"value\":\"146-dfgf\"}]}]}]}";
		final String expressionString1 = "{\"AND\":[{\"AND\":[{\"comparator\":\"lte\",\"type\":\"ROUND_LEVEL_SCORE\",\"value\":500,\"entity_attributes\":[{\"key\":\"roundIndex\",\"value\":1}]},{\"comparator\":\"lte\",\"type\":\"ROUND_LEVEL_SCORE\",\"value\":500,\"entity_attributes\":[{\"key\":\"roundIndex\",\"value\":2}]}]},{\"OR\":[{\"comparator\":\"lte\",\"type\":\"SKILL_LEVEL_SCORE\",\"value\":500,\"entity_attributes\":[{\"key\":\"roundIndex\",\"value\":1},{\"key\":\"skillId\",\"value\":\"123-dff\"}]},{\"comparator\":\"lte\",\"type\":\"SKILL_LEVEL_SCORE\",\"value\":500,\"entity_attributes\":[{\"key\":\"roundIndex\",\"value\":1},{\"key\":\"skillId\",\"value\":\"146-dfgf\"}]}]}]}";
		final JSONObject expression = (JSONObject) parser.parse(expressionString);
		final JSONObject expression1 = (JSONObject) parser.parse(expressionString1);
		final JSONObject result = ruleEvaluationTransformer.generateEvaluatableExpression(
				expression, ruleProcessingData);
		final JSONObject result1 = ruleEvaluationTransformer.generateEvaluatableExpression(
				expression1, ruleProcessingData);
		assertEquals(
				result1.toJSONString(),
				"{\"AND\":[{\"AND\":[{\"comparator\":\"lte\",\"actualValue\":100,\"value\":500},{\"comparator\":\"lte\",\"actualValue\":100,\"value\":500}]},{\"OR\":[{\"comparator\":\"lte\",\"actualValue\":100,\"value\":500},{\"comparator\":\"lte\",\"actualValue\":100,\"value\":500}]}]}");
		assertEquals(
				result.toJSONString(),
				"{\"AND\":[{\"comparator\":\"gte\",\"actualValue\":100,\"value\":500},{\"OR\":[{\"comparator\":\"lte\",\"actualValue\":100,\"value\":500},{\"comparator\":\"lte\",\"actualValue\":100,\"value\":500}]}]}");
	}

}
