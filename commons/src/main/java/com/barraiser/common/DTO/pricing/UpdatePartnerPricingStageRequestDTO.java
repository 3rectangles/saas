/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.DTO.pricing;

import com.barraiser.common.enums.PricingStage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UpdatePartnerPricingStageRequestDTO {
	private String id;
	private String partnerId;
	private PricingStage pricingStage;
	private Integer numberOfInterviewsForDemo;
	private Long applicableFrom;
	private Long applicableTill;
}
