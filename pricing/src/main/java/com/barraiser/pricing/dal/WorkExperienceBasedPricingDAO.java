/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.common.dal.Money;
import com.barraiser.common.enums.RoundType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "work_experience_based_pricing", schema = "pricing")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class WorkExperienceBasedPricingDAO extends BaseModel {
	@Id
	private String id;

	private String partnerId;

	@Enumerated(EnumType.STRING)
	private RoundType roundType;

	private Integer workExperienceLowerBound;

	private Integer workExperienceUpperBound;

	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb", name = "price")
	private Money price;

	private Instant applicableFrom;

	private Instant applicableTill;

	private String createdBy;

	private Instant deprecatedOn;
}
