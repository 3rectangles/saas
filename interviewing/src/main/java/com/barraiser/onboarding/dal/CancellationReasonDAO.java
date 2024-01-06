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

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "cancellation_reason")
public class CancellationReasonDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "reason")
	private String cancellationReason;

	@Column(name = "type")
	private String cancellationType;

	@Column(name = "customer_displayable_reason")
	private String customerDisplayableReason;

	@Column(name = "parent_group_id")
	private String parentGroupId;

	@Column(name = "is_active")
	private Boolean isActive;

	@Column(name = "process_type")
	private String processType;

	@Column(name = "non_reschedulable_reason")
	private Boolean nonReschedulableReason;

	@Column(name = "order_index")
	private Integer orderIndex;
}
