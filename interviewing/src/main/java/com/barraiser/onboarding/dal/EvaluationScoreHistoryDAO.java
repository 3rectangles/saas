/* Copyright (C) BarRaiser Private Limited - All Rights Reserved */
package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.onboarding.interview.evaluation.enums.InterviewProcessType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Table(name = "evaluation_score_history")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EvaluationScoreHistoryDAO extends BaseModel {
	@Id
	private String id;

	@Column(name = "evaluation_id")
	private String evaluationId;

	@Column(name = "scoring_algo_version")
	private String scoringAlgoVersion;

	@Enumerated(EnumType.STRING)
	@Column(name = "process_type")
	private InterviewProcessType processType;

	@Type(type = "jsonb")
	@Column(name = "scores", columnDefinition = "jsonb")
	private Object scores;
}
