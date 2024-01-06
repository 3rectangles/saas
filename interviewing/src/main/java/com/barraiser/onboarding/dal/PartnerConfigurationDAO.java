/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import com.fasterxml.jackson.databind.JsonNode;
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
@Table(name = "partner_configurations")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PartnerConfigurationDAO extends BaseModel {
	@Id
	private String id;

	@Type(type = "jsonb")
	@Column(name = "config", columnDefinition = "jsonb")
	private JsonNode config;

	@Column(name = "partner_id")
	private String partnerId;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "deleted_on")
	private Instant deletedOn;
}
