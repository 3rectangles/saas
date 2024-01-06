/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@Entity(name = "status")
@Table(name = "status")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StatusDAO extends BaseModel implements Serializable {
	@Id
	private String id;

	@Column(name = "internal_status")
	private String internalStatus;

	@Column(name = "entity_type")
	private String entityType;

	@Column(name = "display_status")
	private String displayStatus;

	@Type(type = "jsonb")
	@Column(name = "context", columnDefinition = "jsonb")
	private String context;

	@Column(name = "partner_id")
	private String partnerId;
}
