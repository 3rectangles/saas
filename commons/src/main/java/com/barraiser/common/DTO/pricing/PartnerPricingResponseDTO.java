/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.DTO.pricing;

import com.barraiser.common.enums.PricingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PartnerPricingResponseDTO {
	private String partnerId;
	private PricingType pricingType;
}
