/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.scores;

import java.util.List;
import java.util.Map;

public class NormalisationVersionFetcher {

	private static Map<String, String> evaluationScoreToNormalisationMapping = Map.of(
			"10", "1",
			"11", "2",
			"9", "1",
			"8", "1",
			"7", "1",
			"12", "1",
			"13", "2",
			"14", "2");

	public static List<String> normalisationVersions = List.of("1", "2");

	public static String getNormalisationAlgoVersion(final String scoringAlgoVersion) {
		return evaluationScoreToNormalisationMapping.get(scoringAlgoVersion);
	}
}
