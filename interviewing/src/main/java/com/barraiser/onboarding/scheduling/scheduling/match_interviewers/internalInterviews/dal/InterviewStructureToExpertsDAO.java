/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.scheduling.scheduling.match_interviewers.internalInterviews.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.List;

@Entity
@Table(name = "interview_structure_to_experts")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class InterviewStructureToExpertsDAO extends BaseModel {
	@Id
	private String id;

	private String interviewStructureId;

	@Type(type = "list-array")
	@Column(columnDefinition = "text[]", name = "eligible_experts")
	private List<String> eligibleExperts;
}
