/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement.JobRole.search.dal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class JobRoleSearchDAO {
	@JsonProperty("partner_id")
	private String partnerId;

	@JsonProperty("job_role_id")
	private String jobRoleId;

	@JsonProperty("job_role_version")
	private Integer version;

	@JsonProperty("domain_id")
	private String domainId;

	@JsonProperty("deprecated_on")
	private Instant deprecatedOn;

	@JsonProperty("internal_display_name")
	private String internalDisplayName;

	@JsonProperty("candidate_display_name")
	private String candidateDisplayName;

	@JsonProperty("is_draft")
	private Boolean isDraft;

	@JsonProperty("min_exp")
	private Integer min_exp;

	@JsonProperty("max_exp")
	private Integer max_exp;

}
