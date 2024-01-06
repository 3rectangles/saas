/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Question {
	private String id;

	private String interviewId;

	private String masterQuestionId;

	private String question;

	private String type;

	private String difficulty;

	private Boolean handsOn;

	private Boolean irrelevant;

	/**
	 * Migrating to receiving and
	 * persisting epoch time for question.
	 * Creating new field startTimeEpoch
	 * for backward compatibility
	 */
	private Long startTimeEpoch;

	private Long startTime;

	private Long endTime;

	private Integer serialNumber;

	private List<Feedback> feedbacks;

	private List<Question> followUpQuestions;

	private Boolean isDefault;

	private List<String> tags;

	private String questionCategory;
}
