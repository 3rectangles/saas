/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.confirmation.dto;

import com.barraiser.common.graphql.types.Interview;
import lombok.*;

@Data
public class InterviewConfirmationLifecycleDTO {

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

	private String workflowPath;

	private Integer workflowTurn;

	private String partnerId;
}
