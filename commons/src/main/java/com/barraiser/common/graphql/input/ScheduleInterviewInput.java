/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ScheduleInterviewInput {
	/**
	 * Start date of the interview slot.
	 */
	private Long startDate;
	/**
	 * End date of the interview slot
	 */
	private Long endDate;

	/**
	 * Interviewer with which the interview should be scheduled.
	 */
	private String interviewerId;

	/**
	 * Interview Round
	 */
	private String interviewRound;

	private Boolean isB2B;

	private String interviewId;

	private String schedulingPlatform;

	private String schedulerEmail;

	private String timezone;

	private String meetingLink;

	private Double interviewDuration;

	private String atsInterviewFeedbackLink;
}
