/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class GetJobRoleSkillsValuesInput {
	private String jobRoleDescription;
	private String jobRoleName;
	private String department;
	private Integer noOfRounds;
}
