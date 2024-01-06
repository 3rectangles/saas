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
public class AddBulkEvaluationsCandidateInput {
	private String serialId;
	private String candidateName;
	private String email;
	private String phone;
	private Integer workExperience;
	private String documentId;
	private String documentLink;
	private Boolean forcedAddFlag;
}
