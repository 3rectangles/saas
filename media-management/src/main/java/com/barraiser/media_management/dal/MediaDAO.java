package com.barraiser.media_management.dal;

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
@Table(name = "media")
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
public class MediaDAO extends BaseModel {

    @Id
    private String id;

    @Column(name = "category")
    private String category;

    @Column(name = "format")
    private String format;

    @Column(name = "internal_type")
    private String internalType;

    @Column(name = "context")
    private String context;

    @Column(name = "entity_id")
    private String entityId;

    @Column(name = "entity_type")
    private String entityType;
}
