/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.common.graphql.input.JobRoleInput;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "job_role_history")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class JobRoleHistoryDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "job_role_id")
	private String jobRoleId;

	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	private JobRoleInput jobRoleRawState;
}
