/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.match_interviewers;

import com.barraiser.onboarding.featureToggle.InterviewLevelFeatureToggleManager;
import com.barraiser.onboarding.scheduling.scheduling.match_interviewers.ExpertCostForSchedulingFeatureToggleManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExpertCostForSchedulingFeatureToggleManagerTest {
	@InjectMocks
	private ExpertCostForSchedulingFeatureToggleManager expertCostForSchedulingFeatureToggleManager;
	@Mock
	private InterviewLevelFeatureToggleManager interviewLevelFeatureToggleManager;

	@Test
	public void shouldReturnTrueIfFeatureToggleIsOn() {
		final Map<String, Boolean> featureToggleEntity = Map.of(
				expertCostForSchedulingFeatureToggleManager.FILTER_INTERVIEWER_ON_COST_FEATURE_TOGGLE,
				Boolean.TRUE);
		when(this.interviewLevelFeatureToggleManager.getFeatureToggles("i1"))
				.thenReturn(featureToggleEntity);
		final Boolean isFeatureToggleOn = this.expertCostForSchedulingFeatureToggleManager.isFeatureToggleOn("i1");
		assertTrue(isFeatureToggleOn);
	}

	@Test
	public void shouldReturnFalseIfFeatureToggleIsOff() {
		final Map<String, Boolean> featureToggleEntity = Map.of(
				expertCostForSchedulingFeatureToggleManager.FILTER_INTERVIEWER_ON_COST_FEATURE_TOGGLE,
				Boolean.FALSE);
		final Map<String, String> entity = Map.of(
				"partnerId", "p1");
		when(this.interviewLevelFeatureToggleManager.getFeatureToggles("i1"))
				.thenReturn(featureToggleEntity);
		final Boolean isFeatureToggleOn = this.expertCostForSchedulingFeatureToggleManager.isFeatureToggleOn("i1");
		assertFalse(isFeatureToggleOn);
	}

}
