package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "interview_process_quality")
public class InterviewProcessQualityDAO extends BaseModel {

    @Id
    private String id;

    private String interviewId;

    private Integer taggingQuality;

    private String interviewErrorReported;

    private String errorDescription;

}
