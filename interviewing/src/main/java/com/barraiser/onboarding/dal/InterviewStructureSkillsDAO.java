package com.barraiser.onboarding.dal;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "interview_structure_skills")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class InterviewStructureSkillsDAO extends BaseModel {

    @Id
    private String id;

    private String interviewStructureId;
    private String skillId;
    private Boolean toBeFocussed;
    @Column(name = "is_specific")
    private Boolean isSpecific;
    @Column(name = "is_optional")
    private Boolean isOptional;
}
