/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.common.dal.Money;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "interview_cost", schema = "pricing")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class InterviewCostDetailsDAO extends BaseModel {
	@Id
	private String id;

	private String interviewId;

	private Integer rescheduleCount;

	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb", name = "interview_cost")
	private Money interviewCost;

	private Double usedMargin;

	private String expertId;

	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb", name = "expert_cost_per_hour")
	private Money expertCostPerHour;

	private Double expertMinPricePerHour;

	private Double configuredMargin;
}
