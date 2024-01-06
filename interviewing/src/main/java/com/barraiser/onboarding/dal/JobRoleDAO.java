/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.common.dal.VersionedEntityId;
import com.barraiser.onboarding.audit.AuditListener;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import java.time.Instant;
import java.util.List;

import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@EntityListeners(AuditListener.class)
@Entity
@Table(name = "job_role")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class JobRoleDAO extends BaseModel {

	@JsonUnwrapped // This helps object mapper in deserialization to flat graphql object.
	@EmbeddedId
	private VersionedEntityId entityId;

	@Column(name = "name")
	private String name;

	@Column(name = "domain_id")
	private String domainId;

	@Column(name = "partner_id")
	private String partnerId;

	@Column(name = "company_id")
	private String companyId;

	@Column(name = "category")
	private String category;

	@Column(name = "min_exp")
	private Integer minExp;

	@Column(name = "max_exp")
	private Integer maxExp;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "deprecated_on")
	private Instant deprecatedOn;

	@Column(name = "is_draft")
	private Boolean isDraft;

	@Column(name = "deleted_on")
	private Instant deletedOn;

	@Column(name = "evaluation_process_type")
	private String evaluationProcessType;

	@Column(name = "jd_link")
	private String jdLink;

	@Column(name = "jd_document_id")
	private String jdDocumentId;

	@Column(name = "internal_display_name")
	private String internalDisplayName;

	@Column(name = "candidate_display_name")
	private String candidateDisplayName;

	@Column(name = "default_poc_email")
	private String defaultPocEmail;

	@Column(name = "cutoff_score")
	private Integer cutOffScore;

	@Column(name = "country_code")
	private String countryCode;

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "eligible_countries_of_experts")
	private List<String> eligibleCountriesOfExperts;

	@Column(name = "timezone")
	private String timezone;

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "locations")
	private List<String> locations;

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "teams")
	private List<String> teams;

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "br_status")
	private List<String> brStatus;

	@Column(name = "ats_status")
	private String atsStatus;

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "hiring_managers")
	private List<String> hiringManagers;

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "recruiters")
	private List<String> recruiters;

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "hiring_team_members")
	private List<String> hiringTeamMembers;

	@Column(name = "creation_source")
	private String creationSource;

	@Column(name = "creation_meta")
	private String creationMeta;

	@Column(name = "ext_full_sync")
	private Boolean extFullSync;

	@Column(name = "ext_full_sync_status")
	private String extFullSyncStatus;

	@Column(name = "active_candidates_count_aggregate")
	private Long activeCandidatesCountAggregate; // Contains cumulative count of candidates across all versions

	/**
	 * A default job role is one that gets attached to the partner portal
	 * on creation of that portal incase of a saas free trial.
	 * It is basically a copy of the global default job role with the correct
	 * partner id.
	 * <p>
	 * <p>
	 * NOTE : A job role marked default with no partner id is a global default job
	 * role in the system.
	 */
	@Column(name = "is_default")
	private Boolean isDefault;
}
