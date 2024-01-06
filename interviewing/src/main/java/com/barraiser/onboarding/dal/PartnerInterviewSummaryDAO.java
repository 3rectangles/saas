/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "partner_interview_summary")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PartnerInterviewSummaryDAO extends BaseModel {

	@Id
	@Column(name = "partner_id")
	private String partnerId;

	@Column(name = "average_rating")
	private Double averageRating;

	@Column(name = "total_review_count")
	private Integer totalReviewCount;
}
