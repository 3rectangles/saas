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
@Table(name = "feedback_recommendation")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class FeedbackRecommendationDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "interview_id")
	private String interviewId;

	@Column(name = "feedback_id")
	private String feedbackId;

	@Column(name = "recommendation")
	private String recommendation;

	@Type(type = "jsonb")
	@Column(name = "request", columnDefinition = "jsonb")
	private JsonNode request;
}
