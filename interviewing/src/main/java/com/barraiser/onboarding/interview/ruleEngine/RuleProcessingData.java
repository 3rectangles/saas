/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.ruleEngine;

import lombok.Builder;
import lombok.Data;

import org.json.simple.JSONObject;

@Data
@Builder
public class RuleProcessingData {
	private String entityType;
	private String entityId;
	private String ruleType;
	private JSONObject ruleBody;
	private JSONObject transformedRuleBody;
}
