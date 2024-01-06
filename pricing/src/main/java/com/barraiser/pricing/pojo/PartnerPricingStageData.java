/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing.pojo;

import com.barraiser.common.enums.PricingStage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PartnerPricingStageData {
	private PricingStage pricingStage;
	private Integer numberOfInterviewsForDemo;
	private Long applicableFrom;
	private Long applicableTill;
}
