/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.input;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MapAtsJobPostingToBRJobRoleInput {
	private String partnerId;

	private String atsProvider;

	private List<AtsJobPostingToJobRole> jobPostingToBRJobRoleList;
}
