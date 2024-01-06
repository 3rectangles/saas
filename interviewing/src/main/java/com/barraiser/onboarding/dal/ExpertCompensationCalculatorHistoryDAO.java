package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Table(name = "expert_compensation_calculator_history")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ExpertCompensationCalculatorHistoryDAO extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hour_per_week")
    private Double hourPerWeek;

    @Column(name = "salary_in_lacs")
    private Double salary;

    @Column(name = "min_compensation_in_lacs")
    private Double minCompensation;

    @Column(name = "max_compensation_in_lacs")
    private Double maxCompensation;

    @Column(name = "user_identity")
    private String userIdentity;
}
