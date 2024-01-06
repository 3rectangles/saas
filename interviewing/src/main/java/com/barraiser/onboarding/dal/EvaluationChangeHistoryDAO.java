package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "evaluation_change_history")
public class EvaluationChangeHistoryDAO extends BaseModel {
    @Id
    private String id;

    private String evaluationId;

    private String fieldName;

    private String fieldValue;

    private String createdBy;

    private Instant fieldChangedOn;
}
