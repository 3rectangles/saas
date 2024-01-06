/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import lombok.*;

import java.util.List;

@Data
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Highlight {
	private String id;
	private String description;
	private String title;
	private Integer startTime;
	private Integer endTime;
	private List<HighlightQuestion> questions;
	private List<Skill> skills;
}
