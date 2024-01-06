/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AddBulkEvaluationsInput {
	private String jobRoleId;
	private String pocEmail;
	private String partnerId;
	private List<AddBulkEvaluationsCandidateInput> candidates;
}
