/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science.dal;

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

@Entity
@Table(name = "data_science_user_activity")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DataScienceUserActivityDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "entity_type")
	private String entityType;

	@Column(name = "context")
	private String context;

	@Type(type = "jsonb")
	@Column(name = "payload", columnDefinition = "jsonb")
	private JsonNode payload;
}
