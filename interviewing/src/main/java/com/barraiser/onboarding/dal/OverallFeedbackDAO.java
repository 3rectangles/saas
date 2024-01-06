package com.barraiser.onboarding.dal;


import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

@Entity
@Getter
@Table(name = "overall_feedback")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class OverallFeedbackDAO extends BaseModel {
    @Id
    private String interviewId;
    private String strength;
    private String areasOfImprovement;

    @OneToMany(targetEntity = QcCommentDAO.class, mappedBy = "feedbackId")
    private List<QcCommentDAO> qcComments;
}
