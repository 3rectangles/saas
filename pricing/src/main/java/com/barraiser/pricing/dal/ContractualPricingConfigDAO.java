/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.pricing.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.common.dal.Money;
import com.barraiser.common.enums.PricingType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "contractual_pricing_config", schema = "pricing")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ContractualPricingConfigDAO extends BaseModel {

	@Id
	private String id;

	private String partnerId;

	@Enumerated(EnumType.STRING)
	private PricingType pricingType;

	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb", name = "price")
	private Money price;

	private Double defaultMargin;

	private Instant applicableFrom;

	private Instant applicableTill;

	private Boolean shouldBeConsideredForBilling;

	private String createdBy;

	private Instant deprecatedOn;
}
