/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.feeback;

import com.barraiser.onboarding.dal.NormalisedRatingMapping;
import com.barraiser.onboarding.interview.evaluation.scores.NormalisationVersionFetcher;
import com.barraiser.onboarding.interview.pojo.FeedbackData;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@AllArgsConstructor
public class FeedbackNormalisationUtil {

	public Float getCappedNormalisedRating(final FeedbackData feedback, final String normalisedRatingVersion) {
		final NormalisedRatingMapping normalisedRatingMapping = feedback.getNormalisedRatingMappings() == null ? null
				: feedback.getNormalisedRatingMappings()
						.stream()
						.filter(x -> x.getNormalisationVersion().equals(normalisedRatingVersion))
						.findFirst().orElse(null);
		return normalisedRatingMapping == null ? null : normalisedRatingMapping.getCappedNormalisedRating();
	}

	public Float getCappedNormalisedRatingByScoringAlgoVersion(final FeedbackData feedback,
			final String evaluationScoringVersion) {
		final String normalisedRatingVersion = NormalisationVersionFetcher
				.getNormalisationAlgoVersion(evaluationScoringVersion);
		return this.getCappedNormalisedRating(feedback, normalisedRatingVersion);
	}
}
