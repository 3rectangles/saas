/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import com.barraiser.common.enums.Weightage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Feedback {
	private String id;

	@NotNull(message = "Feedback Category is missing")
	private String categoryId;

	@NotNull(message = "Feedback rating is missing")
	private Float rating;

	private Float weightage;

	@NotNull(message = "Feedback difficulty is missing")
	private String difficulty;

	@NotEmpty(message = "Empty feedback.")
	private String feedback;

	private Boolean handsOn;

	@Valid
	private List<QcComment> qcComments;

	private Boolean looksGood;

	private FeedbackSentiment sentiment;

	private Float modifiedRating;

	private Float normalisedRating;

	private Weightage feedbackWeightage;
}
