/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.barraiser.onboarding.common.Constants.OTHERS_SKILL_ID;
import static com.barraiser.onboarding.common.Constants.SOFT_SKILL_ID;

@Log4j2
@Component
@AllArgsConstructor
public class EvaluationScoreComputationManager {

	public Map<String, Double> getApplicableWeightage(final Map<String, Double> defaultWeightage,
			final Set<String> categoriesInFeedback) {

		if (doesFeedbackOnlyContainImplicitlyIncludedCategories(categoriesInFeedback)) {
			return this.getSkilLWeightagesOfImplicitlyIncludedCategories(defaultWeightage);
		}

		final Set<String> categoriesInFeedbackExcludingOthersCategory = new HashSet<>(categoriesInFeedback);
		if (categoriesInFeedbackExcludingOthersCategory.contains(OTHERS_SKILL_ID)) {
			categoriesInFeedbackExcludingOthersCategory.remove(OTHERS_SKILL_ID);
		}

		final List<String> missingCategories = new ArrayList<>(defaultWeightage.keySet());
		missingCategories.removeAll(categoriesInFeedbackExcludingOthersCategory);
		if (missingCategories.contains(OTHERS_SKILL_ID)) {
			missingCategories.remove(OTHERS_SKILL_ID);
		}

		final Double weightageToDistribute = missingCategories.stream()
				.map(defaultWeightage::get)
				.mapToDouble(Double::doubleValue)
				.sum();

		final Double totalWeightageAcrossCategoriesPresentInFeedback = categoriesInFeedbackExcludingOthersCategory
				.stream()
				.map(defaultWeightage::get)
				.mapToDouble(Double::doubleValue)
				.sum();

		final Map<String, Double> finalWeightage = new HashMap<>();
		categoriesInFeedbackExcludingOthersCategory.forEach(x -> {

			// Distributing the remaining weightage in the ratio of current weightages to
			// categories.
			final Double weightageToAdd = weightageToDistribute
					* (defaultWeightage.get(x) / totalWeightageAcrossCategoriesPresentInFeedback);
			double w = defaultWeightage.get(x) + weightageToAdd;
			w = w * 100;
			w = (int) w / 100D;
			finalWeightage.put(x, w);
		});

		if (categoriesInFeedback.contains(OTHERS_SKILL_ID)) {
			finalWeightage.put(OTHERS_SKILL_ID, defaultWeightage.get(OTHERS_SKILL_ID));
		}

		return finalWeightage;
	}

	/**
	 * Checks if feedback only contains soft skills and others category
	 */
	public static Boolean doesFeedbackOnlyContainImplicitlyIncludedCategories(final Set<String> categories) {
		return (categories.size() == 2) && (categories.contains(OTHERS_SKILL_ID))
				&& (categories.contains(SOFT_SKILL_ID));
	}

	private Map<String, Double> getSkilLWeightagesOfImplicitlyIncludedCategories(
			final Map<String, Double> defaultWeightage) {
		return Map.of(
				OTHERS_SKILL_ID, defaultWeightage.get(OTHERS_SKILL_ID),
				SOFT_SKILL_ID, defaultWeightage.get(SOFT_SKILL_ID));
	}
}
