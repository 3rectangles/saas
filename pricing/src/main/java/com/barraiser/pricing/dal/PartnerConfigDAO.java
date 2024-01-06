/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.common.enums.PricingStage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "partner_config", schema = "pricing")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PartnerConfigDAO extends BaseModel {
	@Id
	private String id;

	private String partnerId;

	@Enumerated(EnumType.STRING)
	private PricingStage stage;

	private Integer numberOfInterviewsForDemo;

	private String createdBy;

	private Instant applicableFrom;

	private Instant applicableTill;
}
