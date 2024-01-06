/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OverallFeedback {
	@NotNull(message = "Overall Feedback - Strength cannot be empty.")
	private Feedback strength;

	@NotNull(message = "Overall Feedback - Areas of Improvement cannot be empty.")
	private Feedback areasOfImprovement;

	private List<Feedback> softSkills;

	private InterviewerRecommendation interviewerRecommendation;
}
