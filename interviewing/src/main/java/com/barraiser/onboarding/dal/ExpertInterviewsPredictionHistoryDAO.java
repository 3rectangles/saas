/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

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

@Entity
@Table(name = "expert_interviews_prediction_history")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ExpertInterviewsPredictionHistoryDAO extends BaseModel {
	@Id
	private String id;

	private String expertId;

	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb", name = "payload")
	private Object payload;

	private Double minCost;

	private Integer predictedNumberOfInterviews;
}
