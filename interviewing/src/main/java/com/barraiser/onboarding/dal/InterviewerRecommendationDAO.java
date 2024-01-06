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
@Getter
@Table(name = "interviewer_recommendation")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class InterviewerRecommendationDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "interview_id")
	private String interviewId;

	@Column(name = "hiring_rating")
	private Integer hiringRating;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "cheating_suspected_remarks")
	private String cheatingSuspectedRemarks;

	@Column(name = "interview_incomplete_remarks")
	private String interviewIncompleteRemarks;
}
