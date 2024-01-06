package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import org.hibernate.annotations.Where;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "interview_to_eligible_experts")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Where(clause = "deleted_on is null")
public class InterviewToEligibleExpertsDAO extends BaseModel {
    @Id private String id;

    private String interviewId;

    private String interviewerId;

    private Long deletedOn;

    private Integer rescheduleCount;
}
