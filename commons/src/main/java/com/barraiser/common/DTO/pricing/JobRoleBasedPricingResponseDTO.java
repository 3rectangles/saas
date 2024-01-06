/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.DTO.pricing;

import com.barraiser.common.dal.Money;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class JobRoleBasedPricingResponseDTO {
	private String jobRoleId;
	private Map<String, InterviewStructurePricing> pricing;

	@Builder(toBuilder = true)
	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	public static class InterviewStructurePricing {
		private Money price;
		private Double margin;
	}
}
