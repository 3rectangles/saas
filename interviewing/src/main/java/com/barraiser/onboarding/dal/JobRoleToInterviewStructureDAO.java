/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;

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
@Table(name = "job_role_to_interview_structure")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class JobRoleToInterviewStructureDAO extends BaseModel {

	@Id
	private String id;

	private String jobRoleId;

	private Integer jobRoleVersion;

	private String interviewRound;

	private String roundName;

	private String interviewStructureId;

	/**
	 * Having interview structure link here as interview structure link kind of
	 * varies with job role
	 * and round. Same interview structure can have different google sheets with
	 * different content
	 * depending upon job role in some sense.
	 */
	private String interviewStructureLink;

	private String problemStatementLink;

	private Integer orderIndex;

	// score above which to consider a candidate
	@Column(name = "cutoff_score")
	private Integer cutOffScore;

	// score below which to reject a candidate
	private Integer thresholdScore;

	private String approvalRuleId;

	private String rejectionRuleId;

	private Boolean isManualActionForRemainingCases;

	@Column(name = "acceptance_cutoff_score")
	private Integer acceptanceCutoffScore;

	@Column(name = "rejection_cutoff_score")
	private Integer rejectionCutoffScore;

	@Column(name = "recommendation_score")
	private Integer recommendationScore;

	@Column(name = "category_rejection_json")
	private String categoryRejectionJSON;

	@Column(name = "interview_cutoff_score")
	private Integer interviewCutoffScore;

}
