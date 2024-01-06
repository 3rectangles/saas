/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.graphql.input;

import com.barraiser.common.graphql.types.Reason;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class CancelInterviewInput {

	private String interviewId;

	private Reason cancellationReason;

	private String cancellationReasonId;
}
