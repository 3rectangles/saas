package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import com.barraiser.common.graphql.types.CustomizableEvaluation;
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
@Getter
@Table(name = "evaluation_history")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class CustomizableEvaluationHistoryDAO extends BaseModel {
    @Id
    private String id;

    @Column(name = "edit_id")
    private String editId;

    @Column(name = "evaluation_id")
    private String evaluationId;

    @Type(type = "jsonb")
    @Column(name = "raw_evaluation")
    private CustomizableEvaluation rawEvaluation;

    @Column(name = "scoring_algo_version")
    private String scoringAlgorithmVersion;

    @Column(name = "user_id")
    private String userId;
}
