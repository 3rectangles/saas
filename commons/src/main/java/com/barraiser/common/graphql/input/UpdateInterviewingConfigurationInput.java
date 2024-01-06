/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdateInterviewingConfigurationInput {
	private String interviewId;

	private String interviewStructureId;

	private String jobRoleId;

	private Integer jobRoleVersion;
}
