package com.barraiser.onboarding.dal;


import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "qc_comment")
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor

public class QcCommentDAO extends BaseModel {
    @Id
    private String id;

    private String commenterId;

    private String feedbackId;

    private String comment;

    @NotFound(action = NotFoundAction.IGNORE)
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "commenterId", insertable = false, updatable = false)
    private UserDetailsDAO commentedBy;


}
