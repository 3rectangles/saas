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
import java.util.List;

@Entity
@Table(name = "partnership_model")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PartnershipModelDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "model_name")
	private String modelName;

	@Column(name = "partnership_type")
	private String partnershipType;

	@Column(name = "description")
	private String description;

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "enabled_features")
	private List<String> enabledFeatures;

	@Type(type = "jsonb")
	@Column(name = "base_config", columnDefinition = "jsonb")
	private JsonNode baseConfig;

}
