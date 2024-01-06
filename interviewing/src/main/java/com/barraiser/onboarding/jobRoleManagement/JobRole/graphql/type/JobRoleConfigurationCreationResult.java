/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.jobRoleManagement.JobRole.graphql.type;

import lombok.Builder;

@Builder(toBuilder = true)
public class JobRoleConfigurationCreationResult {
	private Boolean success;

	private String jobRoleId;

	private Integer jobRoleVersion;
}
