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
public class GetJobRoleInput {
	private String jobRoleId;
	private Integer jobRoleVersion;
	private String partnerId;
	private Boolean onlyAllowLatestVersion;
}
