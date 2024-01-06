/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.pojo;

import com.barraiser.onboarding.dal.NormalisedRatingMapping;
import com.barraiser.common.enums.Weightage;
import lombok.*;

import java.util.List;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class FeedbackData {
	private String id;

	private String categoryId;

	private Float rating;

	private Float weightage;

	private String difficulty;

	private String feedback;

	private Float normalisedRating;

	private String type;

	private String referenceId;

	private Boolean handsOn;

	private String strength;

	private String areasOfImprovement;

	private Boolean looksGood;

	private Integer rescheduleCount;

	private String lengthFlag;

	private List<NormalisedRatingMapping> normalisedRatingMappings;

	private Float modifiedRating;

	private Boolean isSaasFeedback;

	private Weightage feedbackWeightage;
}
