/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.input.training;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder(toBuilder = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TrainingSnippetInput {
	private String id;
	private String userId;
	private String partnerId;
	private String title;
	private String description;
	private Long startTime;
	private Long endTime;
	private String videoId;
	private String videoURL;
	private List<TrainingJobRoleInput> jobRoleList;
	private List<TrainingTagInput> tagList;
}
