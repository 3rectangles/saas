/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.ruleEngine;

import java.util.Map;

public interface RulePopulator {

	String ruleType();

	Object populate(Map<String, Object> valuesMap);
}
