/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class JobRoleCreationResult {
	private String jobRoleId;
	private Integer jobRoleVersion;
	private Boolean success;
	private ArrayList<JobRoleCreationError> errors;
	private String type;
	private List<String> expertsNotFoundForSpecificSkills;
}
