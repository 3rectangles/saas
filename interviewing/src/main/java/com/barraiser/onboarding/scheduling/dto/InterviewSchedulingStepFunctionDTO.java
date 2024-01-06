/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.dto;

import com.barraiser.onboarding.scheduling.scheduling.SchedulingProcessingData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class InterviewSchedulingStepFunctionDTO {
	private SchedulingProcessingData data;
}
