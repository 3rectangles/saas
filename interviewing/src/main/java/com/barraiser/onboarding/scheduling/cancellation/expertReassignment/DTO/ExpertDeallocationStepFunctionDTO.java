/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.cancellation.expertReassignment.DTO;

import com.barraiser.onboarding.scheduling.expert_deallocation.dto.ExpertDeAllocatorData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class ExpertDeallocationStepFunctionDTO {
	private ExpertDeAllocatorData data;
}
