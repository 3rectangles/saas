/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.input;

import com.barraiser.common.enums.PricingType;
import com.barraiser.common.graphql.types.FlatRateBasedPricing;
import com.barraiser.common.graphql.types.WorkExperienceBasedPricing;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PricingConfigInput {
	private String id;
	private FlatRateBasedPricing flatRateBasedPricing;
	private List<WorkExperienceBasedPricing> workExperienceBasedPricing;
	private Long applicableFrom;
	private Long applicableTill;
	private PricingType pricingType;
	private Double defaultMargin;
}
