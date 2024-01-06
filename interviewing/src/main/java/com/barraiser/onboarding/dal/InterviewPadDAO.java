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
@Table(name = "interview_pad")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class InterviewPadDAO extends BaseModel {
    @Id private String id;

    @Column(name = "interview_id")
    private String interviewId;

    @Column(name = "interviewee_pad")
    private String intervieweePad;

    @Column(name = "interviewer_pad")
    private String interviewerPad;
}
