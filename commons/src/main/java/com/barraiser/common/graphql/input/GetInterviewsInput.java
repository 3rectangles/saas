/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class GetInterviewsInput {
	private String status;
	private String interviewId;
	private String partnerId;
	private String jira;
	private String interviewerId;
	private Long startingTime;
	private Long endingTime;
	private List<String> includedStatuses;
	private List<String> excludedStatuses;
	private String evaluationId;
	private Integer page;
	private Integer pageSize;
}
