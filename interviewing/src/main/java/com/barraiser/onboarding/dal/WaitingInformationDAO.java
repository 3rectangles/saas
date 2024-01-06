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
@Table(name = "waiting_information")
public class WaitingInformationDAO extends BaseModel {
	@Id
	private String evaluationId;

	@Column(name = "reason")
	private String reason;

	@Column(name = "waiting_reason_id")
	private String waitingReasonId;

	@Column(name = "updated_by")
	private String updatedBy;
}
