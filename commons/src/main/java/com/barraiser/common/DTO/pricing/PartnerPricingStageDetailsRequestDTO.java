/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.DTO.pricing;

import com.barraiser.common.enums.PricingStage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PartnerPricingStageDetailsRequestDTO {
	private PricingStage pricingStage;
	private Integer numberOfInterviewsForDemo;
	private Long applicableFrom;
	private Long applicableTill;
	private String createdBy;
}
