/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.common.graphql.types;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Skill {
	private String id;
	private String name;
	private Boolean isOptional;
	private Domain domain;
	private Double proficiency;
	private Skill parentSkill;
	private String parentSkillId;
	private String creationSource;
}
