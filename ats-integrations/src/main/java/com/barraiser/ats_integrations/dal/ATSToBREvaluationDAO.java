/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.ats_integrations.dal;

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
@Table(name = "ats_to_br_evaluation")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ATSToBREvaluationDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "br_evaluation_id")
	private String brEvaluationId;

	@Column(name = "partner_id")
	private String partnerId;

	@Column(name = "ats_evaluation_id")
	private String atsEvaluationId;

	@Column(name = "ats_provider")
	private String atsProvider;

	@Column(name = "ats_job_posting_to_br_job_role_id")
	private String atsJobPostingToBRJobRoleId;

	@Column(name = "remote_data")
	private String remoteData;
}
