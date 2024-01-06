/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.onboarding.audit.AuditListener;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@EntityListeners(AuditListener.class)
@Entity
@Table(name = "evaluation")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
public class EvaluationDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "candidate_id")
	private String candidateId;

	@Column(name = "job_role_id")
	private String jobRoleId;

	@Column(name = "job_role_version")
	private Integer jobRoleVersion;

	@Column(name = "company_id")
	private String companyId;

	@Column(name = "partner_id")
	private String partnerId;

	@Column(name = "status")
	private String status;

	@Column(name = "source")
	private String source;

	@Column(name = "default_scoring_algo_version")
	private String defaultScoringAlgoVersion;

	@Column(name = "poc_email")
	private String pocEmail;

	@Column(name = "bgs_created_time_epoch")
	private Long bgsCreatedTimeEpoch;

	@Column(name = "created_by")
	private String createdBy;

	@Column(name = "waiting_reason_id")
	private String waitingReasonId;

	@Column(name = "cancellation_reason_id")
	private String cancellationReasonId;

	@Column(name = "deleted_on")
	private Long deletedOn;

	@Version
	private Long version;

	@ManyToOne(fetch = FetchType.EAGER, cascade = {
			CascadeType.PERSIST,
			CascadeType.MERGE
	})

	@Transient
	private StatusDAO barraiserStatus;

	@ManyToOne(fetch = FetchType.EAGER, cascade = {
			CascadeType.PERSIST,
			CascadeType.MERGE
	})
	@JoinColumn(name = "partner_status_id")
	private StatusDAO partnerStatus;

	@ManyToOne(fetch = FetchType.EAGER, cascade = {
			CascadeType.PERSIST,
			CascadeType.MERGE
	})

	@Transient
	private StatusDAO finalStatus;

	private Double percentile;

	private Boolean isDemo;

	private Boolean isEvaluationScoreUnderReview;

	private String defaultRecommendationVersion;

	@Column(name = "block_candidate_scheduling")
	private Boolean blockCandidateScheduling;

	private String candidateRejectionReason;
}
