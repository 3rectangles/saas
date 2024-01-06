/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@SuperBuilder(toBuilder = true)
@Table(name = "rule")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RuleDAO {
	@Id
	private String id;

	@Column(name = "rule_body")
	private String ruleBody;

	@Column(name = "entity_type")
	private String entityType;
}
