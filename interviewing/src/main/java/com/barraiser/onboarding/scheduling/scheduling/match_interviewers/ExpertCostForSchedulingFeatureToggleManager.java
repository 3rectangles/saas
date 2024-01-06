/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers;

import com.barraiser.onboarding.featureToggle.FeatureToggleManager;
import com.barraiser.onboarding.featureToggle.InterviewLevelFeatureToggleManager;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Map;

@Log4j2
@Component
@AllArgsConstructor
public class ExpertCostForSchedulingFeatureToggleManager {
	public static final String FILTER_INTERVIEWER_ON_COST_FEATURE_TOGGLE = "filter-interviewer-on-cost";
	public static final String MIN_COST_OF_EXPERT_FEATURE_TOGGLE = "min-cost-of-expert";

	private final FeatureToggleManager featureToggleManager;
	private final InterviewLevelFeatureToggleManager interviewLevelFeatureToggleManager;

	public Boolean isFeatureToggleOn(final String interviewId) {
		final Map<String, Boolean> featureToggleForEntity = this.interviewLevelFeatureToggleManager
				.getFeatureToggles(interviewId);
		return featureToggleForEntity.getOrDefault(
				FILTER_INTERVIEWER_ON_COST_FEATURE_TOGGLE, Boolean.FALSE);
	}

	public Boolean isFeatureToggleForMinCostOn(final String expertId) {
		final Map<String, Boolean> featureToggleForEntity = this.featureToggleManager
				.getFeatureToggleForEntity(expertId, "EXPERT", Map.of());
		return featureToggleForEntity.getOrDefault(
				MIN_COST_OF_EXPERT_FEATURE_TOGGLE, Boolean.FALSE);
	}
}
