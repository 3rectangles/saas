/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.data_science.dal;

import com.barraiser.common.dal.BaseModel;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "followup_question_detection")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FollowupQuestionDetectionDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "interview_id")
	private String interviewId;

	@Column(name = "question_id")
	private String questionId;

	@Column(name = "master_question_id")
	private String masterQuestionId;

	@Column(name = "is_followup")
	private Boolean isFollowup;

	@Type(type = "jsonb")
	@Column(name = "request", columnDefinition = "jsonb")
	private JsonNode request;

	@Column(name = "ds_model_version")
	private String dsModelVersion;
}
