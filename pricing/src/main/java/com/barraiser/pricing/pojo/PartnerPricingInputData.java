/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing.pojo;

import com.barraiser.common.enums.PricingType;
import com.barraiser.common.graphql.types.FlatRateBasedPricing;
import com.barraiser.common.graphql.types.JobRoleBasedPricing;
import com.barraiser.common.graphql.types.WorkExperienceBasedPricing;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class PartnerPricingInputData {
	private String id;
	private FlatRateBasedPricing flatRateBasedPricing;
	private List<WorkExperienceBasedPricing> workExperienceBasedPricing;
	private List<JobRoleBasedPricing> jobRoleBasedPricings;
	private Double defaultMargin;
	private Long applicableFrom;
	private Long applicableTill;
	private PricingType pricingType;
	private Boolean shouldPricingBeConsideredInBilling;
}
