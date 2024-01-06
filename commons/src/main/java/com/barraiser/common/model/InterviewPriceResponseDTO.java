/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.model;

import com.barraiser.common.dal.Money;
import com.barraiser.common.enums.PricingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class InterviewPriceResponseDTO {
	private Money maximumInterviewPrice;
	private Double barRaiserMarginPercentage;
	private PricingType pricingType;
	private String partnerConfig;
	private String pricingSpecific;
	private Boolean isDefaultInterviewPrice;
	private String contractualPricingConfig;
}
