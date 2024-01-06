/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing.pojo;

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
public class InterviewPriceData {
	private Money maximumInterviewPrice;
	private PricingType pricingType;
	private String contractualPricingConfig;
	private String pricingSpecific;
	private Boolean isDefaultInterviewPrice;
	private Double barRaiserMarginPercentage;
}
