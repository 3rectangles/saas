/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.input;

import com.barraiser.common.graphql.types.JobRoleBasedPricing;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateJobRoleBasedPricingInput {
	private List<JobRoleBasedPricing> jobRoleBasedPricings;
}
