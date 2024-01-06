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
@Table(name = "ats_job_posting_to_br_job_role")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ATSJobPostingToBRJobRoleDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "partner_id")
	private String partnerId;

	@Column(name = "br_job_role_id")
	private String brJobRoleId;

	@Column(name = "ats_job_posting_id")
	private String atsJobPostingId;

	@Column(name = "ats_provider")
	private String atsProvider;
}
