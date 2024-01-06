/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "reason")
@Where(clause = "is_active is true")
public class ReasonDAO extends BaseModel {

	@Id
	private String id;

	@Column(name = "reason")
	private String reason;

	@Column(name = "type")
	private String type;

	@Column(name = "customer_displayable_reason")
	private String customerDisplayableReason;

	@Column(name = "is_active")
	private Boolean isActive;

	@Column(name = "entity_type")
	private String entityType;

	@Column(name = "non_reschedulable_reason")
	private Boolean nonReschedulableReason;

	@Column(name = "order_index")
	private Integer orderIndex;

	@Column(name = "context")
	private String context;

	private String description;
}
