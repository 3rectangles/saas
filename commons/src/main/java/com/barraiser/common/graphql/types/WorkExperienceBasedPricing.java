/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import com.barraiser.common.dal.Money;
import com.barraiser.common.enums.RoundType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class WorkExperienceBasedPricing {
	private String id;
	private Money price;
	private Integer workExperienceInMonthsUpperBound;
	private Integer workExperienceInMonthsLowerBound;
	private String partnerId;
	private RoundType roundType;
}
