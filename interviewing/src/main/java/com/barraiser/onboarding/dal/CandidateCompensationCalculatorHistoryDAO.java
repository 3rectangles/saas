package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "candidate_compensation_calculator_history")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CandidateCompensationCalculatorHistoryDAO extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "domain_id")
    private String domainId;

    @Column(name = "current_ctc_in_lac")
    private Double currentCTC;

    @Column(name = "work_experience_in_months")
    private Integer workExperience;

    @Column(name = "slope")
    private Double slope;

    @Column(name = "constant")
    private Double constant;

    @Column(name = "user_identity")
    private String userIdentity;
}
