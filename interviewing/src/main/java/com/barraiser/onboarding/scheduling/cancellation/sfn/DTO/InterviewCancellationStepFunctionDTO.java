/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.sfn.DTO;

import com.barraiser.onboarding.scheduling.cancellation.CancellationProcessingData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class InterviewCancellationStepFunctionDTO {
	private CancellationProcessingData data;
}
