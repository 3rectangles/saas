/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types.training;

import com.barraiser.common.graphql.types.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class TrainingSnippet {

	private String id;
	private String userId;
	private String title;
	private String description;
	private Long startTime;
	private Long endTime;
	private String videoId;
	private String videoURL;
	private Instant createdOn;
	private List<TrainingJobRole> jobRoleList;
	private List<TrainingTag> tagList;
}
