/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.lifecycle.DTO;

import com.barraiser.common.graphql.types.Interview;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class InterviewLifecycleDTO {

	private String interviewId;

	private Interview interview;

	/**
	 * This is used during a wait step
	 * to determine what timestamp to
	 * wait until going ahead with
	 * the next step
	 */
	private String timestampToWaitUntil;

	private String interviewConfirmationStatus;
}
