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
@Table(name = "raw_feedback")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class RawFeedbackDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "interview_id")
	private String interviewId;

	@Column(name = "feedback")
	private String feedback;

	@Column(name = "created_by")
	private String createdBy;
}
