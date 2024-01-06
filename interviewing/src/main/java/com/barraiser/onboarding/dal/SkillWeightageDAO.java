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
@Table(name = "skill_weightage")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class SkillWeightageDAO extends BaseModel {
    @Id
    private String id;

    @Column(name = "job_role_id")
    private String jobRoleId;

    @Column(name = "job_role_version")
    private Integer jobRoleVersion;

    @Column(name = "skill_id")
    private String skillId;

    @Column(name = "weightage")
    private Double weightage;

    @Column(name = "evaluation_id")
    private String evaluationId;
}


