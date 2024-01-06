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
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "waiting_reason")
public class WaitingReasonDAO extends BaseModel {

    @Id
    private String id;

    @Column(name = "reason")
    private String reason;

    @Column(name = "category")
    private String category;

    @Column(name = "customer_displayable_reason")
    private String customerDisplayableReason;

    @Column(name = "parent_group_id")
    private String parentGroupId;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "process_type")
    private String processType;
}
