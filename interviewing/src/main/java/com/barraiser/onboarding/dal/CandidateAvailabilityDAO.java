package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "candidate_availability")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Where(clause = "deleted_on is null")
public class CandidateAvailabilityDAO extends BaseModel {
    @Id
    private String id;
    private String userId;
    private Long startDate;
    private Long endDate;
    private String interviewId;
    private Long deletedOn;
}
