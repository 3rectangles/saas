/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.interview.evaluation.search.dal;

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
@Table(name = "evaluation_search")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EvaluationSearchDAO {
	@Id
	private String id;

	@Column(name = "company_id")
	private String companyId;

	@Column(name = "partner_id")
	private String partnerId;

	@Column(name = "deleted_on")
	private Long deletedOn;

	@Column(name = "poc_email")
	private String pocEmail;

	@Column(name = "job_role_id")
	private String jobRoleId;

	@Column(name = "job_role_version")
	private Integer jobRoleVersion;

	@Column(name = "job_role_name")
	private String jobRoleName;

	@Column(name = "domain_id")
	private String domainId;

	@Column(name = "candidate_name")
	private String candidateName;

	@Column(name = "display_status")
	private String displayStatus;

	@Column(name = "created_on")
	private Instant createdOn;

	@Column(name = "status_updated_on")
	private Instant statusUpdatedOn;

	@Column(name = "bgs")
	private Double bgs;

	@Column(name = "is_pending_approval")
	private Boolean isPendingApproval;

	@Column(name = "have_query_for_partner")
	private Boolean haveQueryForPartner;

	@Column(name = "contains_internal_interview")
	private Boolean containsInternalInterview;
}
