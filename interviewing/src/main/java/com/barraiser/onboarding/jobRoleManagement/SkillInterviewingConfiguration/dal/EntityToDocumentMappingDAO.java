package com.barraiser.onboarding.jobRoleManagement.SkillInterviewingConfiguration.dal;

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
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Table(name = "entity_to_document_mapping")
public class EntityToDocumentMappingDAO extends BaseModel {

    @Id
    private String id;

    private String entityId;

    private Integer entityVersion;

    private String entityType;

    private String context;

    private String documentId;

}
