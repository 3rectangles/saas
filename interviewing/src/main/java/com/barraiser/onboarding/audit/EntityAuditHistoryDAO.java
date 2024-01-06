package com.barraiser.onboarding.audit;

import com.barraiser.common.dal.BaseModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "entity_audit_history")
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class EntityAuditHistoryDAO extends BaseModel {

    @Id
    private String id;

    @Column(name = "entity_name")
    private String entityName;

    @Column(name = "entity_id")
    private String entityId;

    @Column(name = "operation")
    private String operation;

    @Column(name = "raw_entity_state")
    @Type(type = "jsonb")
    private Object rawEntityState;

    @Column(name = "operated_by")
    private String operatedBy;

}
