/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.common.graphql.types.RecommendationType;
import com.barraiser.onboarding.audit.AuditListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@EntityListeners(AuditListener.class)
@Entity
@Table(name = "evaluation_recommendation")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EvaluationRecommendationDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "evaluation_id")
	private String evaluationId;

	@Column(name = "recommendation_algo_version")
	private String recommendationAlgoVersion;

	@Enumerated(EnumType.STRING)
	@Column(name = "recommendation_type")
	private RecommendationType recommendationType;

	@Column(name = "screening_cut_off")
	private Integer screeningCutOff;
}
