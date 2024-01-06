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
import java.time.Instant;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "currency")
@Deprecated(forRemoval = true)
public class CurrencyDAO extends BaseModel {
	@Id
	private String id;

	private String currencyCode;

	private String symbol;

	@Column(name = "inr_conversion_rate")
	private Double INRConversionRate;

	private Instant disabledOn;
}
