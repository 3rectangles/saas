/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JoinFormula;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.WhereJoinTable;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
/* @Super */
@Builder(toBuilder = true)

@Table(name = "interview_structure")
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class InterviewStructureDAO extends BaseModel {
	@Id
	private String id;

	private String name;

	private String domainId;

	private Integer minExperience;

	private Integer maxExperience;

	private String jiraIssueId;

	private Integer expertJoiningTime;

	private Integer duration;

	private Boolean allSkillsFound;

	private String interviewFlowLink;

	private Boolean isBrRound;

	private String interviewFlow;

	/**
	 * An interview structure that is either copied from the global default
	 * interview structure or partner default interview structure and hence marked
	 * default.
	 */
	private Boolean isDefault;
}
