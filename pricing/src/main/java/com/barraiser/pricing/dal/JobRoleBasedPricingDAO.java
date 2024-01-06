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
import java.time.Instant;

@Entity
@Table(name = "job_role_based_pricing", schema = "pricing")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class JobRoleBasedPricingDAO extends BaseModel {
	@Id
	private String id;

	private String jobRoleId;

	private String interviewStructureId;

	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb", name = "price")
	private Money price;

	private Double margin;

	private Instant applicableFrom;

	private Instant applicableTill;

	private String createdBy;
}
