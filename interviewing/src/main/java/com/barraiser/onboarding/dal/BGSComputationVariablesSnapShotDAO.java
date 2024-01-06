/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.onboarding.interview.evaluation.scores.pojo.ComputeEvaluationScoresData;
import com.barraiser.onboarding.interview.evaluation.enums.InterviewProcessType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "bgs_computation_variables_snapshot")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BGSComputationVariablesSnapShotDAO extends BaseModel {
	@Id
	private String id;

	private String entityId;

	private String entityType;

	@Type(type = "jsonb")
	@Column(name = "payload", columnDefinition = "jsonb")
	private ComputeEvaluationScoresData payload;

	@Enumerated(EnumType.STRING)
	private InterviewProcessType processType;

	private String scoringAlgoVersion;
}
