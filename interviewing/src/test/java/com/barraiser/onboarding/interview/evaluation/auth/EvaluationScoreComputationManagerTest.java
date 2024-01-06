/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.auth;

import com.barraiser.onboarding.common.Constants;
import com.barraiser.onboarding.interview.evaluation.EvaluationScoreComputationManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;
import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class EvaluationScoreComputationManagerTest {

	@InjectMocks
	private EvaluationScoreComputationManager evaluationScoreComputationManager;

	/**
	 * Without others with no missing category: regression check
	 */
	@Test
	public void testScenario1() {

		final Map<String, Double> skilLWeightageMap = Map.of(
				"skill_coding", 25.0,
				"skill_LLD", 45.0,
				"skill_db_design", 25.0,
				"52", 5.0);

		final Set<String> categoriesInFeedback = Set.of("skill_coding", "skill_LLD", "skill_db_design", "52");

		Map<String, Double> adjustedWeightages = this.evaluationScoreComputationManager
				.getApplicableWeightage(skilLWeightageMap, categoriesInFeedback);
		Assert.assertEquals(4, adjustedWeightages.size());
		Assert.assertEquals((Double) 25.0, adjustedWeightages.get("skill_coding"));
		Assert.assertEquals((Double) 45.0, adjustedWeightages.get("skill_LLD"));
		Assert.assertEquals((Double) 25.0, adjustedWeightages.get("skill_db_design"));
		Assert.assertEquals((Double) 5.0, adjustedWeightages.get("52"));
	}

	/**
	 * Without others : with missing categories
	 */
	@Test
	public void testScenario2() {

		final Map<String, Double> skilLWeightageMap = Map.of(
				"skill_coding", 25.0,
				"skill_LLD", 45.0,
				"skill_db_design", 25.0,
				"52", 5.0);

		final Set<String> categoriesInFeedback = Set.of("skill_coding", "skill_db_design", "52");

		Map<String, Double> adjustedWeightages = this.evaluationScoreComputationManager
				.getApplicableWeightage(skilLWeightageMap, categoriesInFeedback);

		Assert.assertEquals(3, adjustedWeightages.size());
		Assert.assertEquals((Double) 45.45, adjustedWeightages.get("skill_coding"));
		Assert.assertEquals((Double) 45.45, adjustedWeightages.get("skill_db_design"));
		Assert.assertEquals((Double) 9.09, adjustedWeightages.get("52"));
	}

	/**
	 * With others : without missing categories .
	 */
	@Test
	public void testScenario3() {

		final Map<String, Double> skilLWeightageMap = Map.of(
				"skill_coding", 25.0,
				"skill_LLD", 45.0,
				"skill_db_design", 25.0,
				"52", 5.0,
				Constants.OTHERS_SKILL_ID, 0.0);

		final Set<String> categoriesInFeedback = Set.of("skill_coding", "skill_LLD", "skill_db_design", "52",
				Constants.OTHERS_SKILL_ID);

		Map<String, Double> adjustedWeightages = this.evaluationScoreComputationManager
				.getApplicableWeightage(skilLWeightageMap, categoriesInFeedback);

		Assert.assertEquals(5, adjustedWeightages.size());
		Assert.assertEquals((Double) 25.0, adjustedWeightages.get("skill_coding"));
		Assert.assertEquals((Double) 45.0, adjustedWeightages.get("skill_LLD"));
		Assert.assertEquals((Double) 25.0, adjustedWeightages.get("skill_db_design"));
		Assert.assertEquals((Double) 5.0, adjustedWeightages.get("52"));
		Assert.assertEquals((Double) 0.0, adjustedWeightages.get(Constants.OTHERS_SKILL_ID));
	}

	/**
	 * With others : with no missing category except that no feedback was tagged
	 * with others
	 */
	@Test
	public void testScenario4() {

		final Map<String, Double> skilLWeightageMap = Map.of(
				"skill_coding", 25.0,
				"skill_LLD", 45.0,
				"skill_db_design", 25.0,
				"52", 5.0,
				Constants.OTHERS_SKILL_ID, 0.0);

		final Set<String> categoriesInFeedback = Set.of("skill_coding", "skill_LLD", "skill_db_design", "52");

		Map<String, Double> adjustedWeightages = this.evaluationScoreComputationManager
				.getApplicableWeightage(skilLWeightageMap, categoriesInFeedback);

		Assert.assertEquals(4, adjustedWeightages.size());
		Assert.assertEquals((Double) 25.0, adjustedWeightages.get("skill_coding"));
		Assert.assertEquals((Double) 45.0, adjustedWeightages.get("skill_LLD"));
		Assert.assertEquals((Double) 25.0, adjustedWeightages.get("skill_db_design"));
		Assert.assertEquals((Double) 5.0, adjustedWeightages.get("52"));
	}

	/**
	 * With others : with missing category except AND no feedback was tagged with
	 * others
	 */
	@Test
	public void testScenario5() {

		final Map<String, Double> skilLWeightageMap = Map.of(
				"skill_coding", 25.0,
				"skill_LLD", 45.0,
				"skill_db_design", 25.0,
				"52", 5.0,
				Constants.OTHERS_SKILL_ID, 0.0);

		final Set<String> categoriesInFeedback = Set.of("skill_coding", "skill_LLD", "52");

		Map<String, Double> adjustedWeightages = this.evaluationScoreComputationManager
				.getApplicableWeightage(skilLWeightageMap, categoriesInFeedback);

		Assert.assertEquals(3, adjustedWeightages.size());
		Assert.assertEquals((Double) 33.33, adjustedWeightages.get("skill_coding"));
		Assert.assertEquals((Double) 60.0, adjustedWeightages.get("skill_LLD"));
		Assert.assertEquals((Double) 6.66, adjustedWeightages.get("52"));
	}

	/**
	 * With others : with all questions tagged with Others , apart form Soft Skills
	 */
	@Test
	public void testScenario6() {

		final Map<String, Double> skilLWeightageMap = Map.of(
				"skill_coding", 25.0,
				"skill_LLD", 45.0,
				"skill_db_design", 25.0,
				"52", 5.0,
				Constants.OTHERS_SKILL_ID, 95.0);

		final Set<String> categoriesInFeedback = Set.of("52", Constants.OTHERS_SKILL_ID);

		Map<String, Double> adjustedWeightages = this.evaluationScoreComputationManager
				.getApplicableWeightage(skilLWeightageMap, categoriesInFeedback);

		Assert.assertEquals(2, adjustedWeightages.size());
		Assert.assertEquals((Double) 5.0, adjustedWeightages.get("52"));
		Assert.assertEquals((Double) 95.0, adjustedWeightages.get(Constants.OTHERS_SKILL_ID));
	}

	/**
	 * With others only : with all questions tagged with Others , apart form Soft
	 * Skills
	 */
	@Test
	public void testScenario7() {

		final Map<String, Double> skilLWeightageMap = Map.of(
				"52", 5.0,
				Constants.OTHERS_SKILL_ID, 95.0);

		final Set<String> categoriesInFeedback = Set.of("52", Constants.OTHERS_SKILL_ID);

		Map<String, Double> adjustedWeightages = this.evaluationScoreComputationManager
				.getApplicableWeightage(skilLWeightageMap, categoriesInFeedback);

		Assert.assertEquals(2, adjustedWeightages.size());
		Assert.assertEquals((Double) 5.0, adjustedWeightages.get("52"));
		Assert.assertEquals((Double) 95.0, adjustedWeightages.get(Constants.OTHERS_SKILL_ID));
	}

	/**
	 * CASE : Fasttrack interview mostly only soft skills will be there.
	 * Weightage given for other categories also.
	 */
	@Test
	public void testScenario8() {

		final Map<String, Double> skillWeightageMap = Map.of(
				"skill_coding", 25.0,
				"skill_LLD", 70.0,
				"52", 5.0,
				Constants.OTHERS_SKILL_ID, 95.0);

		final Set<String> categoriesInFeedback = Set.of("52");

		Map<String, Double> adjustedWeightages = this.evaluationScoreComputationManager
				.getApplicableWeightage(skillWeightageMap, categoriesInFeedback);

		Assert.assertEquals(1, adjustedWeightages.size());
		Assert.assertEquals((Double) 100.0, adjustedWeightages.get("52"));
	}

	/**
	 * CASE : All questions tagged with others. This will happen for SaaS interviews
	 * which
	 * will be used for quickstart for new customers. No need for JR prep in detail.
	 */
	@Test
	public void testScenario9() {

		final Map<String, Double> skillWeightageMap = Map.of(
				"52", 5.0,
				Constants.OTHERS_SKILL_ID, 95.0);

		final Set<String> categoriesInFeedback = Set.of(Constants.OTHERS_SKILL_ID);

		Map<String, Double> adjustedWeightages = this.evaluationScoreComputationManager
				.getApplicableWeightage(skillWeightageMap, categoriesInFeedback);

		Assert.assertEquals(1, adjustedWeightages.size());
		Assert.assertEquals((Double) 95.0, adjustedWeightages.get(Constants.OTHERS_SKILL_ID));
	}

}
