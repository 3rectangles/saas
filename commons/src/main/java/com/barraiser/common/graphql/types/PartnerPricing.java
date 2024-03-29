/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import com.barraiser.common.enums.PricingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PartnerPricing {
	private PricingType pricingType;
}
