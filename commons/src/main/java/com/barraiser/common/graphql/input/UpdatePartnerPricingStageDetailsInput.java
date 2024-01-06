/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.input;

import com.barraiser.common.enums.PricingStage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UpdatePartnerPricingStageDetailsInput {
	private String partnerId;
	private PricingStage pricingStage;
	private Integer numberOfInterviewsForDemo;
	private Long applicableFrom;
	private Long applicableTill;
}
