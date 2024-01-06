/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "interview_cost")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class InterviewCostDAO extends BaseModel {

	@Id
	private String id;

	@Column(name = "interview_id")
	private String interviewId;

	@Column(name = "interviewer_id")
	private String interviewerId;

	@Column(name = "interviewer_base_cost")
	private Double interviewerBaseCost;

	@Column(name = "multiplier")
	private Double multiplier;

	@Column(name = "total_amount")
	private Double totalAmount;

	@Column(name = "cost_logic")
	private String costLogic;

	@Column(name = "cancellation_logic")
	private String cancellationLogic;

	@Column(name = "payment_type")
	private String paymentType;

	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb", name = "interview_snapshot")
	private InterviewDAO interviewSnapshot;

	@Column(name = "reschedule_count")
	private Integer rescheduleCount;

	@Column(name = "currency_id")
	private String currencyId;
}
