package com.barraiser.onboarding.interview.scoring.dal;

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
@Table(name = "interview_score")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class InterviewScoreDAO extends BaseModel {
    @Id
    private String id;

    @Column(name = "interview_id")
    private String interviewId;

    @Column(name = "skill_id")
    private String skillId;

    @Column(name = "score")
    private Double score;

    @Column(name = "scoring_algo_version")
    private String scoringAlgoVersion;

    @Column(name = "weightage")
    private Double weightage;

}
