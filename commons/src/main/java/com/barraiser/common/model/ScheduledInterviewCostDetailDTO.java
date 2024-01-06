/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.model;

import com.barraiser.common.dal.Money;
import com.barraiser.common.enums.PricingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Id;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ScheduledInterviewCostDetailDTO {
	private Money interviewCost;
	private Double usedMargin;
	private String expertId;
	private Money expertCostPerHour;
	private Double expertMinPricePerHour;
	private Double configuredMargin;
}
