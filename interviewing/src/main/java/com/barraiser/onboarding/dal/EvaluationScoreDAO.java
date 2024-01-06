package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.onboarding.audit.AuditListener;
import com.barraiser.onboarding.interview.evaluation.enums.InterviewProcessType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@EntityListeners(AuditListener.class)
@Entity
@Table(name = "evaluation_score")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class EvaluationScoreDAO extends BaseModel {
    @Id
    private String id;

    @Column(name = "evaluation_id")
    private String evaluationId;

    @Column(name = "skill_id")
    private String skillId;

    @Column(name = "score")
    private Double score;

    @Enumerated(EnumType.STRING)
    @Column(name = "process_type")
    private InterviewProcessType processType;

    @Column(name = "scoring_algo_version")
    private String scoringAlgoVersion;

    @Column(name = "weightage")
    private Double weightage;
}
