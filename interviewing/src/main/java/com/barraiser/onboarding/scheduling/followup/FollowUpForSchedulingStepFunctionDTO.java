/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.followup;

import com.barraiser.common.graphql.types.Evaluation;
import lombok.Data;

@Data
public class FollowUpForSchedulingStepFunctionDTO {
	private String evaluationId;

	private Evaluation evaluation;

	private String timestampToWaitUntil;

	private Integer workflowTurn;

	private Long expiryTime;

	private String partnerId;

	private Long followUpDate;

	private String followUpStatus;

	private Boolean ivrCallPicked;
}
