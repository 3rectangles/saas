/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import lombok.*;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class NormalisedRatingMapping {
	private String normalisationVersion;
	private Float normalisedRating;
	private Float rating;
	private Float cappedNormalisedRating;
}
